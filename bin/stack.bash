#!/bin/bash

set -eu

################################################################################
# FUNCTIONS
################################################################################

  Show_Help()
  {
    echo && cat ./docs/help/stack.txt && echo
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

          mitmproxy | mitmweb | mitmdump )

            Run_Mitm_Proxy ${background_mode} ${@}

            exit $?
          ;;

        esac
      done

      Show_Help
}

Main "${@}"
