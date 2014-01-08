#!/bin/sh
set -e

if [ "$TRAVIS_PULL_REQUEST" = false ]; then
  if [ cat pom.xml | grep version | head -2 | tail -1 | cut -d\> -f2 | cut -d\< -f1 | grep SNAPSHOT ]; then
    python addServer.py && mvn deploy --settings ~/.m2/mySettings.xml
  else
    mvn package
  fi
else
  mvn package
fi