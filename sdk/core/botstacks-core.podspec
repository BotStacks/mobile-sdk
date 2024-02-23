Pod::Spec.new do |spec|
    spec.name                     = 'botstacks-core'
    spec.version                  = '0.0.1'
    spec.homepage                 = 'https://botstacks.ai'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'BotStacks'
    spec.license                  = 'MIT'
    spec.summary                  = 'Internal BotStacks Core KMP SDK'
    spec.vendored_frameworks      = 'build/cocoapods/framework/botstacks_core.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '15.0'
    spec.dependency 'Giphy', '2.2.8'
    spec.dependency 'GoogleMaps', '8.4.0'
    spec.dependency 'Sentry', '8.20.0'
                
    if !Dir.exist?('build/cocoapods/framework/botstacks_core.framework') || Dir.empty?('build/cocoapods/framework/botstacks_core.framework')
        raise "

        Kotlin framework 'botstacks_core' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :sdk:core:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':sdk:core',
        'PRODUCT_MODULE_NAME' => 'botstacks_core',
    }
                
    spec.script_phases = [
        {
            :name => 'Build botstacks-core',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
    spec.resources = ['build/compose/ios/botstacks-core/compose-resources']
end