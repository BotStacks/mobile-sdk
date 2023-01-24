package io.inappchat.inappchat.cache.database.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "domain_filter",
  indices = [Index(value = ["tenant_id"])],
  foreignKeys = [ForeignKey(
    entity = Tenant::class,
    parentColumns = ["id"],
    childColumns = ["tenant_id"],
    onDelete = ForeignKey.CASCADE
  )]
)

data class DomainFilter(
  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "domain") var domain: String,
  @ColumnInfo(name = "tenant_id") var tenantId: String? = null,
  @ColumnInfo(name = "action_type") var actionType: String? = null
)