# CURRENCY CONVERTER DEMO

This repository is part of the articles:

* [Steal that API key with a Man in the Middle Attack](https://blog.approov.io/steal-that-api-key-with-a-man-in-the-middle-attack).
* [Securing HTTPS with Certificate Pinning on Android](https://blog.approov.io/securing-https-with-certificate-pinning-on-android).
* [Bypassing Certificate Pinning](https://blog.approov.io/bypassing-certificate-pinning).
* [How to Protect Against Certificate Pinning Bypassing](https://blog.approov.io/how-to-protect-against-certificate-pinning-bypassing).


## HOW TO USE

Please read one of the linked articles to better understand the purpose and the scope where you will be using the
Currency Converter Demo.

### The Stack

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

### The Mobile App Demo

Please see the [README](./mobile-app/android/README.md) for the instructions.
