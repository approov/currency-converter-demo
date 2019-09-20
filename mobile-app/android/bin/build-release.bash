#!/bin/sh

set -eu

Main()
{
    # thw cache here can also cause problems during a build
    rm -rf ./app/.externalNativeBuild

    # When using the CLI the task `clean` is important, otherwise you may run a build that does not match your last
    # changes.
    #
    # When using Android studio it asks you to click `sync now` for each time a gradle file changes, but that is not
    # enough, you also need to do Build > Clean Project.
    #
    # IMPORTANT: always build the final release APK from the CLI.
    ./gradlew clean
    ./gradlew cleanBuildCache
    ./gradlew build

    printf "\n\nINSTALL THE APK IN YOUR DEVICE WITH: adb install app/build/outputs/apk/release/app-release.apk\n\n"
}

Main
