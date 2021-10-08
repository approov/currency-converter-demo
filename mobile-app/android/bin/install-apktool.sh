#!/bin/sh

set -eu

Main()
{
  local APKTOOL_VERSION=${1:-2.6.0}

  sudo curl -o /usr/local/bin/apktool https://raw.githubusercontent.com/iBotPeaches/Apktool/master/scripts/linux/apktool

  sudo chmod +x /usr/local/bin/apktool

  sudo curl -Lo /usr/local/bin/apktool.jar https://bitbucket.org/iBotPeaches/apktool/downloads/apktool_"${APKTOOL_VERSION}".jar
}

Main "${@}"
