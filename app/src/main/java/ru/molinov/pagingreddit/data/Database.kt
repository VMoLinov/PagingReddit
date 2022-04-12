package ru.molinov.pagingreddit.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RemoteKeys::class, Data::class], version = 2, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun getDao(): Dao
    abstract fun remoteKeyDao(): RemoteKeysDao
}
