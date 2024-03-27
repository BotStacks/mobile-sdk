Pod::Spec.new do |spec|
    spec.name                     = 'BotStacksChatSDK'
    spec.version                  = '1.0.3-SNAPSHOT'
    spec.homepage                 = 'https://botstacks.ai'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'BotStacks'
    spec.license                  = 'MIT'
    spec.summary                  = 'BotStacks Core Chat SDK'
    spec.vendored_frameworks      = 'build/BotStacksChatSDK.xcframework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '15.0'

    spec.dependency 'Giphy', '2.2.8'
    spec.dependency 'GoogleMaps', '8.4.0'
end