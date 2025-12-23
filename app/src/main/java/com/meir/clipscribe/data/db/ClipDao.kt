package com.meir.clipscribe.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipDao {
  @Query("SELECT * FROM clips ORDER BY createdAt DESC")
  fun observeAll(): Flow<List<ClipEntity>>

  @Query("SELECT * FROM clips WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): ClipEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(entity: ClipEntity)
}
