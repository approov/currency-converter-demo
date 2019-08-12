#!/bin/sh

set -eu

Main()
{
    apktool --force --output .local/apktool/decoded-apk decode app/build/outputs/apk/release/app-release.apk
}

Main
