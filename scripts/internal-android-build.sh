#!/usr/bin/env bash

date="$(date '+%m.%d.%y')"

./gradlew assembleDebug

build_num=$1

if [ -z "$build_num" ]
then
   apk_name="sample-android-${date}-debug.apk"
else
  apk_name="sample-android-${date}_$build_num-debug.apk"
fi

outputDir="$(pwd)/sample-android/build/outputs/apk/debug"

mv "${outputDir}/sample-android-debug.apk" "${outputDir}/${apk_name}"