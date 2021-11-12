#!/bin/bash

set -eu

Main()
{

    ####################################################################################################################
    # DEFAULTS
    ####################################################################################################################

        local base_dir=.local/apktool/decoded-apk
        local unpacked_build_dir="${base_dir}"/build
        local signed_repackaged_apk="${base_dir}/repackaged-and-signed.apk"
        local unaligned_apk="${base_dir}"/unaligned.apk
        local aligned_apk="${base_dir}"/aligned.apk
        local keystore_file=./.local/approov.keystore.jks
        local android_build_tools_version=28.0.3
        local android_build_tools_path=~/Android/Sdk/build-tools/28.0.3


    ####################################################################################################################
    # INPUT
    ####################################################################################################################

        for input in "${@}"; do
            case "${input}" in
                --bp | --build-tools-path )
                    local android_build_tools_path="${2? Missing adroid build tools version.}"
                ;;
                --ks | --keystore )
                    local keystore_file="${2? Missing path to keystore file.}"
                ;;
            esac
        done


    ####################################################################################################################
    # EXECUTION
    ####################################################################################################################

        rm -rf "${unaligned_apk}" "${aligned_apk}" "${signed_repackaged_apk}" "${unpacked_build_dir}"

        apktool b -f "${base_dir}" -o "${unaligned_apk}"

        "${android_build_tools_path}"/zipalign -v -p 4 "${unaligned_apk}" "${aligned_apk}"
        "${android_build_tools_path}"/apksigner sign --ks "${keystore_file}" --out "${signed_repackaged_apk}" "${aligned_apk}"

        rm -rf "${unaligned_apk}" "${aligned_apk}" "${unpacked_build_dir}"

        printf "\n\nINSTALL THE APK IN YOUR DEVICE WITH:"
        printf "\nadb install -r ${signed_repackaged_apk}\n\n"
}

Main
