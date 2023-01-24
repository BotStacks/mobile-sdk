package io.inappchat.inappchat.cache.database.entity


import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by DK on 2019-08-14.
 */

@Entity(
  tableName = "chat_thread",
  indices = [Index(value = ["id", "parent_msg_id"], unique = true), Index(value = ["msg_unique_id", "thread_id"], unique = true)],
  foreignKeys = [ForeignKey(
    entity = SingleChat::class,
    parentColumns = ["id"],
    childColumns = ["parent_msg_id"],
    onDelete = ForeignKey.CASCADE
  )]
) // @Index(value = { "recipient_id", "sender_id" })


data class ChatThread @JvmOverloads constructor(
  @PrimaryKey @ColumnInfo(name = "id") val id: String,
  @ColumnInfo(name = "createdAt") var createdAt: Long? = null,
  @ColumnInfo(name = "message") var message: String? = null,
  @ColumnInfo(name = "read") var read: Int? = -1, // defines message type
  @ColumnInfo(name = "type") var type: String? = null,
  @ColumnInfo(name = "sender_id") var senderAppUserId: String? = null,
  // @ColumnInfo(name = "recipient_id")
  // String recipientId = "";
  @ColumnInfo(name = "thread_id") var threadId: String? = null,
  @ColumnInfo(name = "next_msg_id") var nextMessageId: String? = null,
  @ColumnInfo(name = "last_msg_id") var lastMessageId: String? = null,
  @ColumnInfo(name = "status") var status: String? = null,
  @ColumnInfo(name = "msg_type") var msgType: String? = null,
  @ColumnInfo(name = "msg_unique_id") var msgUniqueId: String? = null,
  //Media share
  @ColumnInfo(name = "media_path") var mediaPath: String? = null,
  @ColumnInfo(name = "media_thumbnail") var mediaThumbnail: String? = null,
  @ColumnInfo(name = "media_name") var mediaName: String? = null,
  @ColumnInfo(name = "local_media_path") var localMediaPath: String? = null,
  @ColumnInfo(name = "download_status") var downloadStatus: String? = null,
  //contact share
  @ColumnInfo(name = "contact_name") var contactName: String? = null,
  @ColumnInfo(name = "phone_contact_list") val phoneContactList: List<PhoneContact>? = null,
  @ColumnInfo(name = "email_contact_list") val emailContactList: List<EmailContact>? = null,
  @ColumnInfo var isStarredChat: String? = null,
  @ColumnInfo(name = "gif_path") var gifPath: String? = null,
  @Embedded var location: Location? = null,
  @ColumnInfo(name = "send_to_channel") var sendToChannel: Int? = null,
  //Message Delete
  @ColumnInfo(name = "delete_type") var deleteType: String? = null,
  //Non -overridden attributes
  @ColumnInfo(name = "parent_msg_id") var parentMsgId: String? = null, //foreign key constraint, this is single_chat app generated id.
  @ColumnInfo(name = "update_type") var updateType: String? = null,
  //client side timeStamp
  @ColumnInfo(name = "client_created_at") var clientCreatedAt: Long? = null,
  //Custom Data (extra data from app) in JSON format
  @ColumnInfo(name = "custom_data") var customData: String? = null,
  //Report Message
  @ColumnInfo(name = "chat_report_id") var chatReportId: String? = null,
  @ColumnInfo(name = "report_type") var reportType: String? = null,
  @ColumnInfo(name = "reason") var reason: String? = null,
  //Mentions Data in JSON format
  @ColumnInfo(name = "mentions") var mentions: String? = null,
  @ColumnInfo(name = "mentioned_users") var mentionedUsers: String? = null
)
