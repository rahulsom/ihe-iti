#!/bin/sh -e
#
# Uploads released artifacts
#

export GPGKEY=$(gpg --list-keys | grep pub | tr -s " " | cut -d " " -f 2 | cut -d/ -f2 | tail -1)
echo GPGKEY=$GPGKEY
export IHEVER=$(cat pom.xml| xpath /project/version/text\(\))
PASSPHRASE=$1

if [ "$PASSPHRASE" = "" ]; then
  echo "Passphrase is required"
  exit 1
fi

REPOCONF="-Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
    -DrepositoryId=sonatype-nexus-staging \
    -Dgpg.passphrase=${PASSPHRASE}"

mvn clean install

echo mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/ihe-iti-${IHEVER}.jar
mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/ihe-iti-${IHEVER}.jar

echo mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/ihe-iti-${IHEVER}-sources.jar -Dclassifier=sources
mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/ihe-iti-${IHEVER}-sources.jar -Dclassifier=sources

echo mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/ihe-iti-${IHEVER}-javadoc.jar -Dclassifier=javadoc
mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/ihe-iti-${IHEVER}-javadoc.jar -Dclassifier=javadoc
