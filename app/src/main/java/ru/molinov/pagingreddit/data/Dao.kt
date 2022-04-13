package ru.molinov.pagingreddit.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Post>)

    @Query("SELECT * FROM Post")
    fun getData(): PagingSource<Int, Post>

    @Query("DELETE FROM Post")
    fun clearAll()
}
