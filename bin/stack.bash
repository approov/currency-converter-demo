#!/bin/bash

set -eu

################################################################################
# FUNCTIONS
################################################################################

  Show_Help()
  {
    echo && cat ./docs/help/stack.txt && echo
  }

  Setup_Stack_And_Mobile_App()
  {
    ############################################################################
    # VARS
    ############################################################################

      # For a production secret is preferable to use at least: openssl rand -base64 256 | tr -d '\n'
      local api_key=$( echo "currency-converter-demo-with-certificate-pinning" | openssl dgst -binary -sha256 | openssl enc -base64 )

      local mobile_app_api_key_file=./mobile-app/android/app/src/main/cpp/api_key.h


    ############################################################################
    # EXECUTION
    ############################################################################

      if [ ! -f .env ]; then
        cp -v .env.example .env
        sed -i "s|base64-encoded-api-key|${api_key}|g" .env
      fi

      if [ ! -f "${mobile_app_api_key_file}" ]; then
        cp -v "${mobile_app_api_key_file}".example "${mobile_app_api_key_file}"
        sed -i "s|base64-encoded-api-key|${api_key}|g" "${mobile_app_api_key_file}"
      fi
  }

  Docker_Container_Is_Running()
  {
    sudo docker container ls -a | grep -q "${container_name}" -

    return $?
  }

  Run_Mitm_Proxy()
  {
    ############################################################################
    # INPUT
    ############################################################################

      local background_mode="${1? Missing background mode !!!}"

      shift 1


    ############################################################################
    # VARS
    ############################################################################

      local container_name="${PWD##*/}_proxy"


    ############################################################################
    # EXECUTION
    ############################################################################

      sudo docker run \
        --rm \
        ${background_mode} \
        ${PORT_MAP} \
        --name "${container_name}" \
        --volume ~/.mitmproxy:/home/mitmproxy/.mitmproxy \
        mitmproxy/mitmproxy:6.0.2 ${@} # mitmproxy --showhost --view-filter "approov"
  }


  Stop_Docker_Container()
  {
    ############################################################################
    # INPUT
    ############################################################################

      local docker_stack_name="${1? Missing docker stack name!!!}"


    ############################################################################
    # VARS
    ############################################################################

      local container_name="${PWD##*/}_${docker_stack_name}"


    ##########################################################################
    # EXECUTION
    ##########################################################################

      if Docker_Container_Is_Running "${container_name}"; then
        sudo docker container stop "${container_name}"
      fi
  }

################################################################################
# MAIN
################################################################################

  Main()
  {
    ############################################################################
    # CONSTANTS
    ############################################################################

      local MITM_DOCKER_IMAGE=mitmproxy/mitmproxy:6.0.2
      local PORT_MAP=""


    ############################################################################
    # VARS
    ############################################################################

      local background_mode="-it"


    ############################################################################
    # INPUT
    ############################################################################

      for input in "${@}"; do
        case "${input}" in

          -d | --detach )
            local background_mode="--detach"
            shift 1
            ;;

          -h | --help )
            Show_Help
            exit $?
            ;;

          -it )
            local background_mode="-it"
            shift 1
          ;;

          --wifi-ip-address )
            local PORT_MAP="--publish ${2? Missing the WiFi ip address}:8080:8080"
            shift 2
          ;;

          install )
            sudo docker pull "${MITM_DOCKER_IMAGE}"
            exit $?
          ;;

          mitmproxy | mitmweb | mitmdump )

            Run_Mitm_Proxy ${background_mode} ${@}

            exit $?
          ;;

          setup )
            Setup_Stack_And_Mobile_App
            exit 0
          ;;

        esac
      done

      Show_Help
}

Main "${@}"
