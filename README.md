# CURRENCY CONVERTER DEMO

This repository is part of the article [Steal that API key with a man in the middle attack](https://blog.approov.io/steal-that-api-key-with-a-man-in-the-middle-attack).

## REQUIREMENTS

### Android

Your Android development setup needs to have installed CMake and the NDK bundle that you can install by following the [Android docs](https://developer.android.com/studio/projects/install-ndk).

> **NOTE:** If after the mobile app doesn't build then it's probably because the NDK version that was installed is missing the `platforms` dir, but the error show is only about a `null` pointer exception. This is a known bug in the Gradle building systems that goes back several years. To fix it just create an empty `platforms` dir with:
>
>```bash
>mkdir -p /Android/Sdk/ndk-bundle/platforms
>```


## HOW TO USE

Please read the linked article to understand the purpose and how to use the
Currency Converter Demo.

In order to make easy to follow the demo, a bash script is included:

```bash
  ./stack

CURRENCY CONVERTER DEMO STACK

  A stack that uses Docker containers for running the Currency Converter Demo.


USAGE:

  ./stack [options] [command] [arguments]


OPTIONS:

  -d, --detached          Run the docker container detached from the terminal:
                            $ ./stack -d <command> <arguments>
                            $ ./stack --detached <command> <arguments>

  -h, --help              Shows the help for the Elixir CLI and Stack:
                            $ ./stack -h
                            $ ./stack --help

  -it                     Runs the docker container attached to the terminal:
                            $ ./stack -it <command> <arguments>


COMMANDS / ARGUMENTS:

  down                    Stops and removes all running containers:
                            $ ./stack down
                            $ ./stack down proxy

  install                 Pulls the official Docker image for mitmproxy:
                            $ ./stack install

  mitmweb [options]       Runs a mitmweb command:
                            $ ./stack mitmweb --listen-host <wifi.ip.address> --web-iface <wifi.ip.address>

  up <arguments>          Starts the docker containers for running the stack:
                            $ ./stack up proxy <wifi.ip.address>

```

Mainly the bash script is a wrapper around Docker commands.
