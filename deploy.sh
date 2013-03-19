#!/bin/sh
#
# Uploads released artifacts
#

export GPGKEY=$(gpg --list-keys | grep pub | tr -s " " | cut -d " " -f 2 | cut -d/ -f2 | tail -1)
export IHEVER=$(cat pom.xml| xpath /project/version/text\(\))
echo mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=../pom.xml -Dfile=ihe-iti-${IHEVER}.jar
echo mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=../pom.xml -Dfile=ihe-iti-${IHEVER}-sources.jar -Dclassifier=sources
echo mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=../pom.xml -Dfile=ihe-iti-${IHEVER}-javadoc.jar -Dclassifier=javadoc
