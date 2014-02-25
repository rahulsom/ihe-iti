#!/bin/sh -e
#
# Uploads released artifacts
#

export GPGKEY=$(gpg --list-keys | grep pub | tr -s " " | cut -d " " -f 2 | cut -d/ -f2 | tail -1)
echo GPGKEY=$GPGKEY
export LIB_VER=$(cat pom.xml| xpath /project/version/text\(\))
export LIB_NAME=$(cat pom.xml| xpath /project/artifactId/text\(\))
PASSPHRASE=$1

if [ "$PASSPHRASE" = "" ]; then
  echo "Passphrase is required"
  exit 1
fi

REPOCONF="-Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
    -DrepositoryId=sonatype-nexus-staging \
    -Dgpg.passphrase=${PASSPHRASE}"

mvn clean install

echo mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/${LIB_NAME}-${LIB_VER}.jar
mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/${LIB_NAME}-${LIB_VER}.jar

echo mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/${LIB_NAME}-${LIB_VER}-sources.jar -Dclassifier=sources
mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/${LIB_NAME}-${LIB_VER}-sources.jar -Dclassifier=sources

echo mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/${LIB_NAME}-${LIB_VER}-javadoc.jar -Dclassifier=javadoc
mvn gpg:sign-and-deploy-file $REPOCONF -DpomFile=pom.xml -Dfile=target/${LIB_NAME}-${LIB_VER}-javadoc.jar -Dclassifier=javadoc
