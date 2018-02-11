#!/bin/bash

# Build
./gradlew build jacocoTestReport coveralls || exit 1

if [ "$TRAVIS_REPO_SLUG" == "wupdigital/android-maven-publish" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
	# Publish the artifacts
	./gradlew publish publishPlugins -Dgradle.publish.key=$GRADLE_KEY -Dgradle.publish.secret=$GRADLE_SECRET || exit 1
	# Push the groovydoc
	./gradlew gitPublishPush || exit 1
fi