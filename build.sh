#!/bin/sh
set -e

if [[ "$TRAVIS_PULL_REQUEST" = false ]]; then 
  python addServer.py && mvn deploy --settings ~/.m2/mySettings.xml
else
  mvn package
fi