package ru.molinov.pagingreddit.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Data::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun getDao(): Dao
}
