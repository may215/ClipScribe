package com.meir.clipscribe.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clips")
data class ClipEntity(
  @PrimaryKey val id: String,
  val url: String,
  val videoId: String,
  val startSec: Int,
  val endSec: Int,
  val createdAt: Long,
  val title: String,
  val transcriptMd: String,
  val lastPlaybackMs: Long
)
