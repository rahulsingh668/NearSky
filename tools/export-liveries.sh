#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
task_classes=$(mktemp -d)
trap 'rm -rf "$task_classes"' EXIT
javac -encoding UTF-8 -d "$task_classes" app/src/main/java/com/nearsky/tv/AirlineNames.java app/src/main/java/com/nearsky/tv/LiveryDesign.java tools/ExportLiveries.java
java -cp "$task_classes" ExportLiveries "${1:-NearSky-liveries.zip}"
