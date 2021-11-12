#!/bin/sh

set -eu

Main()
{
    local _apk_path="${1:-app/build/outputs/apk/release/app-release.apk}"

    rm -rf .local/apktool/decoded-apk

    apktool --force --output .local/apktool/decoded-apk decode "${_apk_path}"

    printf "\n\n---> APK decoded into: .local/apktool/decoded-apk\n\n"
}

Main "${@}"
