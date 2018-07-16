#!/bin/bash

# Build sample project
./gradlew build publishToMavenLocal -c sample/settings.gradle || exit 1
# Build plugin and publish test coverage
./gradlew build jacocoTestReport coveralls -c plugin/settings.gradle || exit 1

if [[ "$TRAVIS_REPO_SLUG" == "wupdigital/android-maven-publish" && "$TRAVIS_PULL_REQUEST" == "false" && ("$TRAVIS_BRANCH" == "master" || "$TRAVIS_BRANCH" == support/* ) ]]; then
	# Publish the artifacts
	./gradlew publish publishPlugins -Dgradle.publish.key=$GRADLE_KEY -Dgradle.publish.secret=$GRADLE_SECRET -c plugin/settings.gradle || exit 1
	# Push the groovydoc
	./gradlew gitPublishPush -c plugin/settings.gradle || exit 1
fi