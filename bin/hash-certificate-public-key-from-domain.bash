#!/bin/bash
# Heavily inspired on:
#   * https://medium.com/@appmattus/android-security-ssl-pinning-1db8acb6621e#ecea

set -eu

Main()
{
    local domain="${1? Missing domain name to extract and hash the certificate public key !!!}"

    local domain="${domain##*://}"

    local certs=$( openssl s_client -servername "${domain}" -host "${domain}" -port 443 -showcerts </dev/null 2>/dev/null | sed -n '/Certificate chain/,/Server certificate/p' )

    local rest=$certs

    while [[ "$rest" =~ '-----BEGIN CERTIFICATE-----' ]]; do

        cert="${rest%%-----END CERTIFICATE-----*}-----END CERTIFICATE-----"
        rest=${rest#*-----END CERTIFICATE-----}

        local certificate_name="$( echo "$cert" | grep 's:' | sed 's/.*s:\(.*\)/\1/' )"

        if [ -n "${certificate_name}" ]; then
            printf "\nCERTIFICATE NAME:\n\n${certificate_name} \n\n"
        fi

        printf "\nCERTIFICATE PUBLIC KEY HASH:\n\n"

        echo "$cert" |
            openssl x509 -pubkey -noout |
            openssl rsa -pubin -outform der 2>/dev/null |
            openssl dgst -sha256 -binary |
            openssl enc -base64

        echo

        exit 0

    done
}

Main ${@}
