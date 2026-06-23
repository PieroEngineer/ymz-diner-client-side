package com.yummizi.core.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

// Placeholder entity for initial compilation. Add feature entities here later.
@Entity(tableName = "local_user_placeholder")
data class LocalUserEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Database(
    entities = [LocalUserEntity::class], // Append new feature entities to this array
    version = 1,
    exportSchema = true // Production best practice: keeps track of migrations for version control
)
abstract class YummiziDatabase : RoomDatabase() {
    // Abstract DAO declarations will be placed here as features are integrated
}