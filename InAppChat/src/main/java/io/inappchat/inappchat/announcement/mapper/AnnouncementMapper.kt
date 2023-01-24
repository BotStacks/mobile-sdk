package io.inappchat.inappchat.announcement.mapper

import io.inappchat.inappchat.mqtt.model.AnnouncementResponse

object AnnouncementMapper {

  @JvmStatic
  fun transform(announcementResponse: AnnouncementResponse): AnnouncementRecord {
    return if (announcementResponse.announcement.group == null) {
      AnnouncementRecord(details = announcementResponse.announcement.details)
    } else {
      AnnouncementRecord(
        details = announcementResponse.announcement.details,
        groupId = announcementResponse.announcement.group?.groupId,
        groupType = announcementResponse.announcement.group?.groupType,
        name = announcementResponse.announcement.group?.name
      )
    }
  }
}