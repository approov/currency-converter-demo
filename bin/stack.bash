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

        esac
      done

      Show_Help
}

Main "${@}"
