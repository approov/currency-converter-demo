#!/bin/sh

set -eu

Add_Local_Property()
{
  local property="${1? Missing property name.}"

  local value="${2? Missing property value}"

  if ! grep -q "${property}" ./local.properties; then
    printf "\n${property}=${value}" >> ./local.properties
  fi
}

Main()
{
  mkdir -p .local

  local keystore_path=.local/approov.keystore.jks

  if [ ! -f "${keystore_path}" ]; then
    keytool \
      -v \
      -genkey \
      -keystore "${keystore_path}" \
      -alias approov \
      -keyalg RSA \
      -keysize 2048 \
      -validity 10000

  else
    printf "\n---> The keystore already exists at: ${keystore_path}\n"
  fi

  printf "\n---> Adding, if not already present, properties to your ./local.properties file\n"

  # to avoid the error "grep: ./local.properties: No such file or directory"
  touch ./local.properties

  Add_Local_Property "android.keystore.path" "../.local/approov.keystore.jks"
  Add_Local_Property "android.private.key.alias" "approov"
  Add_Local_Property "android.keystore.password" "YOUR_PASSWORD_HERE"
  Add_Local_Property "android.private.key.password" "YOUR_PASSWORD_HERE"

  printf "\n---> Edit your ./local.properties file and add the passwords you have used when you first created the keystore.\n\n"
}

Main
