#!/bin/sh

# file       : cmstest.sh
#
# author     : Paul Koster
#
# date       : 2011-08-21
# version    : 0.3
#
# description: cmstest.sh creates and verifies examples compliant to the IHE DEN profile
#
# a self-signed certificate and keys may be create using:
#  openssl genrsa -out privatekey.pem 2048
#  openssl req -new -x509 -key privatekey.pem -out certificate.pem -days 6000

# openssl-1.0.0.d supports most options
#  proper handling of the -econtent_type option requires a small patch 
#  password support requires the HEAD development branch or recent SNAPSHOT (+patch)
#
#  download 
#  apply patch
#   cd <openssl-path>
#   patch -n1 <econtent.diff
#  build
#   make config && make

# set path(s)
export OPENSSL=openssl-1.0.0d/apps/openssl
export OPENSSL2=openssl-SNAP-20110716/apps/openssl

echo "Testing OpenSSL -> OpenSSL"
echo "**************************"

#0. add mime header
echo "0 (mime)"
cat mime.txt doc >doc.mime

#1.A sign data
echo "1A (sign)"
$OPENSSL cms -sign -binary -nodetach -noattr -md sha256 -in doc.mime -outform der -out doc.signed -signer certificate.pem -inkey privatekey.pem 

#1.B digest data
echo "1B (digest)"
$OPENSSL cms -binary -digest_create -md sha256 -in doc.mime -outform der -out doc.digested 

#2.A encrypt with certificate
echo "2A SIGNATURE (RSA, SHA256) ENCRYPTION (CERTIFICATE, AES256)"
$OPENSSL cms -encrypt -binary -econtent_type pkcs7-signedData -aes256 -in doc.signed -keyform pem -inkey privatekey.pem -outform der -out doc.signed.encrypted_cert certificate.pem

echo "   DIGEST    (     SHA256) ENCRYPTION (CERTIFICATE, AES192)"
$OPENSSL cms -encrypt -binary -econtent_type pkcs7-digestData -aes192 -in doc.digested -keyform pem -inkey privatekey.pem -outform der -out doc.digested.encrypted_cert certificate.pem


#2.B encrypt with password
echo "2B SIGNATURE (RSA, SHA256) ENCRYPTION (PASSWORD   , AES128)"
$OPENSSL2 cms -encrypt -binary -econtent_type pkcs7-signedData -aes128 -in doc.signed -aes256 -pwri_password test -outform der -out doc.signed.encrypted_pass 

echo "   DIGEST    (     SHA256) ENCRYPTION (PASSWORD   , AES256)"
$OPENSSL2 cms -encrypt -binary -econtent_type pkcs7-digestData -aes256 -in doc.digested -aes256 -pwri_password test -outform der -out doc.digested.encrypted_pass

#2.C encrypt with symmetric key
echo "2C SIGNATURE (RSA, SHA256) ENCRYPTION (SHAREDKEY  , AES192)"
$OPENSSL cms -encrypt -binary -econtent_type pkcs7-signedData -aes192 -in doc.signed   -aes256 -secretkey 010203040506070801020304050607080102030405060708 -secretkeyid 01 -outform der -out doc.signed.encrypted_symm 

echo "   DIGEST    (     SHA256) ENCRYPTION (SHAREDKEY  , AES128)"
$OPENSSL cms -encrypt -binary -econtent_type pkcs7-digestData -aes128 -in doc.digested -aes256 -secretkey 01020304050607080102030405060708 -secretkeyid 01 -outform der -out doc.digested.encrypted_symm

#3.A decrypt with certificate
echo "3A (decrypt)"
$OPENSSL cms -decrypt -inform der -in doc.signed.encrypted_cert   -keyform pem -inkey recipientprivk.pem -outform der -out doc.signed.encrypted_cert.decrypted
$OPENSSL cms -decrypt -inform der -in doc.digested.encrypted_cert -keyform pem -inkey recipientprivk.pem -outform der -out doc.digested.encrypted_cert.decrypted

#3.B decrypt
echo "3B (decrypt)"
$OPENSSL2 cms -decrypt -inform der -in doc.signed.encrypted_pass   -pwri_password test -outform der -out doc.signed.encrypted_pass.decrypted
$OPENSSL2 cms -decrypt -inform der -in doc.digested.encrypted_pass -pwri_password test -outform der -out doc.digested.encrypted_pass.decrypted

#3.C decrypt
echo "3C (decrypt)"
$OPENSSL cms -decrypt -inform der -in doc.signed.encrypted_symm   -secretkey 010203040506070801020304050607080102030405060708 -outform der -out doc.signed.encrypted_symm.decrypted
$OPENSSL cms -decrypt -inform der -in doc.digested.encrypted_symm -secretkey 01020304050607080102030405060708 -outform der -out doc.digested.encrypted_symm.decrypted

#4.A verify signature
echo "4A (verify)"
$OPENSSL cms -verify -inform der -in doc.signed.encrypted_cert.decrypted -outform der -out doc.signed.encrypted_cert.decrypted.verified -signer certificate.pem -CAfile certificate.pem
$OPENSSL cms -verify -inform der -in doc.signed.encrypted_pass.decrypted -outform der -out doc.signed.encrypted_pass.decrypted.verified -signer certificate.pem -CAfile certificate.pem
$OPENSSL cms -verify -inform der -in doc.signed.encrypted_symm.decrypted -outform der -out doc.signed.encrypted_symm.decrypted.verified -signer certificate.pem -CAfile certificate.pem

#4.B verify digest
echo 4B "(verify)"
$OPENSSL cms -digest_verify -inform der -in doc.digested.encrypted_cert.decrypted -outform der -out doc.digested.encrypted_cert.decrypted.verified 
$OPENSSL cms -digest_verify -inform der -in doc.digested.encrypted_pass.decrypted -outform der -out doc.digested.encrypted_pass.decrypted.verified 
$OPENSSL cms -digest_verify -inform der -in doc.digested.encrypted_symm.decrypted -outform der -out doc.digested.encrypted_symm.decrypted.verified 

#5. remove mime
echo "5 (done)"

echo "***************************"
echo "Verify input & output files"
echo "***************************"
echo "(same hash value indicates success)"
md5sum doc.mime doc.*.decrypted.verified

