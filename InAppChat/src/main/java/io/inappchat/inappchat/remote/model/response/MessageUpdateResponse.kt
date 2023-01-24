package io.inappchat.inappchat.remote.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.inappchat.inappchat.remote.core.ValidItem


data class MessageUpdateResponse(
  @SerializedName("chats") @Expose val chats: ArrayList<MessageResponse>? = null
) : ValidItem {
  override fun isValid(): Boolean {
    return true
  }
}