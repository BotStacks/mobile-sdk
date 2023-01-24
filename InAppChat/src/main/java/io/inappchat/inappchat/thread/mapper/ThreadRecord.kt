package io.inappchat.inappchat.thread.mapper

import io.inappchat.inappchat.chat.mapper.MessageRecord
import io.inappchat.inappchat.group.mapper.GroupRecord
import io.inappchat.inappchat.user.mapper.UserRecord

/** Created by DK on 01/03/19.  */

data class ThreadRecord @JvmOverloads constructor(
  var id: String,
  var name: String,
  var type: String,
  var messageCount: Int,
  var read: Int,
  var unReadCount: Int,
  var creationDate: Long,
  var hasDeleted: Int,
  var lastMessage: MessageRecord,
  var recipient: UserRecord? = null,
  var groupRecipient: GroupRecord? = null,
  var notificationSettings: String
)