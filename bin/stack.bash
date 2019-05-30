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
        --name "${container_name}" \
        --net host \
        --volume ~/.mitmproxy:/home/mitmproxy/.mitmproxy \
        "${MITM_DOCKER_IMAGE}" ${@}
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

      local MITM_DOCKER_IMAGE=mitmproxy/mitmproxy:4.0.4


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

          down )
            shift 1

            case "${@}" in

              proxy )
                Stop_Docker_Container "proxy"
              ;;

              * )
                Stop_Docker_Container "proxy"
              ;;

            esac

            exit $?
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

          up )
            shift 1

            case "${1:-}" in

              proxy )
                shift 1

                local wifi_ip_address="${1? Missing wifi ip address for proxy server !!!}"

                Run_Mitm_Proxy \
                  "${background_mode}" \
                  mitmweb \
                  --web-iface "${wifi_ip_address}" \
                  --listen-host "${wifi_ip_address}"
              ;;

              * )
                Show_Help
                exit $?
              ;;
            esac

          ;;

        esac
      done

      Show_Help
}

Main "${@}"
