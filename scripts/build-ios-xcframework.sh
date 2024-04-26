#!/bin/bash

buildKmp=$1

if [ "$buildKmp" = "true" ]; then
  echo "Building KMP XCFramework"
  echo ""
  ./gradlew :chat-sdk:podPublishDebugXCFramework
  echo ""
fi

cd ios/BotStacksChatSDK

rm -rf build

echo "Building wrapper XCFramework"
echo ""

xcodebuild archive \
-scheme BotStacksChatSDK \
-workspace BotStacksChatSDK.xcworkspace \
-configuration Release \
-destination 'generic/platform=iOS' \
-archivePath 'build/BotStacksChatSDK.framework-iphoneos.xcarchive' \
SKIP_INSTALL=NO \
BUILD_LIBRARY_FOR_DISTRIBUTION=YES
xcodebuild archive \
-scheme BotStacksChatSDK \
-workspace BotStacksChatSDK.xcworkspace \
-configuration Release \
-destination 'generic/platform=iOS Simulator' \
-archivePath 'build/BotStacksChatSDK.framework-iphonesimulator.xcarchive' \
SKIP_INSTALL=NO \
BUILD_LIBRARY_FOR_DISTRIBUTION=YES
xcodebuild -create-xcframework \
-framework 'build/BotStacksChatSDK.framework-iphonesimulator.xcarchive/Products/Library/Frameworks/BotStacksChatSDK.framework' \
-framework 'build/BotStacksChatSDK.framework-iphoneos.xcarchive/Products/Library/Frameworks/BotStacksChatSDK.framework' \
-output 'build/BotStacksChatSDK.xcframework'

echo ""
echo "Done"
echo ""

cd -
