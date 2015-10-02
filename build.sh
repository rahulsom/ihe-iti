#!/bin/sh
set -e

if [ "$TRAVIS_PULL_REQUEST" = false ]; then
  if [ "$TRAVIS_BRANCH" = "develop" ]; then
    python addServer.py && ./mvnw deploy --settings ~/.m2/mySettings.xml
  else
    ./mvnw package
  fi
else
  ./mvnw package
fi
