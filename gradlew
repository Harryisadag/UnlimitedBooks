#!/bin/bash
set -e
DIR="$( cd "$( dirname "$0" )" && pwd )"
java -Xmx1G -Xms128M -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"