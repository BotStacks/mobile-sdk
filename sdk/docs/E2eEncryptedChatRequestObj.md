
# E2eEncryptedChatRequestObj

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**threadId** | **kotlin.String** | Thread Id. This is exclusive peer to recipientAppUserId. |  [optional]
**recipientAppUserId** | **kotlin.String** | App user Id of receiver. This is exclusive peer to threadId. |  [optional]
**sendereRTCUserId** | **kotlin.String** | eRTC user id of source user |  [optional]
**msgType** | **kotlin.String** | message type. it can be text/image/audio/video/gif/file |  [optional]
**metadata** | [**kotlin.Any**](.md) | JSON object which can be used for client reference in request/response context. Server will not do any processing on this object. eg. { \&quot;abc\&quot; : \&quot;def\&quot; } |  [optional]
**encryptedChatList** | [**kotlin.collections.List&lt;EncryptedChatObj&gt;**](EncryptedChatObj.md) | List of user+device wise eencrypted chat objects. |  [optional]
**senderKeyDetails** | [**E2eEncryptedChatRequestObjSenderKeyDetails**](E2eEncryptedChatRequestObjSenderKeyDetails.md) |  |  [optional]
**replyThreadFeatureData** | [**ReplyThreadSchemaChatRequest**](ReplyThreadSchemaChatRequest.md) |  |  [optional]
**forwardChatFeatureData** | [**ForwardChatSchemaChatRequest**](ForwardChatSchemaChatRequest.md) |  |  [optional]
**media** | [**MediaSchema**](MediaSchema.md) |  |  [optional]
**mentions** | [**kotlin.collections.List&lt;MentionSchema&gt;**](MentionSchema.md) |  |  [optional]
**msgCorrelationId** | **kotlin.String** | Client generated unique identifier used to trace message delivery till receiver. |  [optional]
**senderTimeStampMs** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | epoch timestamp (in ms) of message creation generated on sender device |  [optional]
**customData** | [**kotlin.Any**](.md) | JSON object which can be used for customer specific data which is not supported in InAppChat chat model. eg. { \&quot;abc\&quot; : \&quot;def\&quot; } |  [optional]


