/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.inappchat.sdk.models

import io.inappchat.sdk.models.ReceiverReturnCode

import com.squareup.moshi.Json

/**
 * 
 *
 * @param keyId Key identifier.
 * @param deviceId Device identifier.
 * @param publicKey public Key of associated device
 * @param eRTCUserId user eRTCUserId.
 * @param returnCode 
 */


data class ChatStatusKeyListInner (

    /* Key identifier. */
    @Json(name = "keyId")
    val keyId: kotlin.String,

    /* Device identifier. */
    @Json(name = "deviceId")
    val deviceId: kotlin.String,

    /* public Key of associated device */
    @Json(name = "publicKey")
    val publicKey: kotlin.String,

    /* user eRTCUserId. */
    @Json(name = "eRTCUserId")
    val eRTCUserId: kotlin.String,

    @Json(name = "returnCode")
    val returnCode: ReceiverReturnCode? = null

)
