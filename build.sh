#!/usr/bin/env bash
# build all sources into out/ and produce a runnable jar
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt
rm sources.txt

cat > out/Manifest.txt <<EOF
Main-Class: com.typingmaster.Main
EOF

cd out
jar cfm ../TypingMaster.jar Manifest.txt com
cd ..
echo "built TypingMaster.jar"
