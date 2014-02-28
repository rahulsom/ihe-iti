#!/bin/sh
set -e

if [ "$TRAVIS_PULL_REQUEST" = false ]; then
  if [ "$TRAVIS_BRANCH" = "develop" ]; then
    python addServer.py && mvn deploy --settings ~/.m2/mySettings.xml
  else
    mvn package
  fi
else
  mvn package
fi