#!/bin/sh

set -eu

Main()
{
    apktool --force --output .local/apktool/decoded-apk decode app/build/outputs/apk/release/app-release.apk

    printf "\n\n---> APK decoded into: .local/apktool/decoded-apk\n\n"
}

Main
