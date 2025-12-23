package com.meir.clipscribe.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ClipEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun clipDao(): ClipDao
}
