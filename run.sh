#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
[ -f TypingMaster.jar ] || ./build.sh
java -jar TypingMaster.jar
