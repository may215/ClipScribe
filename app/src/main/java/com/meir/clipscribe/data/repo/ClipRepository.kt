package com.meir.clipscribe.data.repo

import android.content.Context
import androidx.room.Room
import com.meir.clipscribe.data.db.AppDatabase
import com.meir.clipscribe.data.db.ClipEntity
import kotlinx.coroutines.flow.Flow

class ClipRepository(context: Context) {
  private val db = Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    "clipscribe.db"
  ).build()

  private val dao = db.clipDao()

  fun observeAll(): Flow<List<ClipEntity>> = dao.observeAll()
  suspend fun getById(id: String): ClipEntity? = dao.getById(id)
  suspend fun upsert(entity: ClipEntity) = dao.upsert(entity)
}
