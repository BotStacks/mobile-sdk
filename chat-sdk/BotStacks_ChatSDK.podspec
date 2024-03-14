Pod::Spec.new do |spec|
    spec.name                     = 'BotStacks_ChatSDK'
    spec.version                  = '1.0.4-SNAPSHOT'
    spec.homepage                 = 'https://botstacks.ai'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'BotStacks'
    spec.license                  = 'MIT'
    spec.summary                  = 'BotStacks Core Chat SDK'
    spec.vendored_frameworks      = 'build/cocoapods/framework/BotStacks_ChatSDK.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '15.0'
    spec.dependency 'Giphy', '2.2.8'
    spec.dependency 'GoogleMaps', '8.4.0'
                
    if !Dir.exist?('build/cocoapods/framework/BotStacks_ChatSDK.framework') || Dir.empty?('build/cocoapods/framework/BotStacks_ChatSDK.framework')
        raise "

        Kotlin framework 'BotStacks_ChatSDK' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :chat-sdk:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':chat-sdk',
        'PRODUCT_MODULE_NAME' => 'BotStacks_ChatSDK',
    }
                
    spec.script_phases = [
        {
            :name => 'Build BotStacks_ChatSDK',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end