#!/bin/sh
if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]; then
  echo "This is a pull request. No deployment will be done."
  exit 0
fi
if [[ "$TRAVIS_BRANCH" != "beta_release" ]]; then
  echo "Testing on a branch other than beta_release. No deployment will be done."
  exit 0
fi

dir=$PWD
parent="$(dirname "$dir")"

echo "Building?"
$parent/gradlew build