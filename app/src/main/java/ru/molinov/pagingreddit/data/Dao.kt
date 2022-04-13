package ru.molinov.pagingreddit.data

import android.os.Parcelable
import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.Dao
import kotlinx.parcelize.Parcelize

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Data>)

    @Query("SELECT * FROM Data")
    fun getData(): PagingSource<Int, Data>

    @Query("DELETE FROM Data")
    fun clearAll()
}

@Parcelize
data class ServerData(
    val data: ServerDataChildren
) : Parcelable

@Parcelize
data class ServerDataChildren(
    val children: List<ServerDataPage>
) : Parcelable

@Entity
@Parcelize
data class ServerDataPage(
    val data: Data
) : Parcelable

@Parcelize
@Entity
data class Data(
    @PrimaryKey(autoGenerate = true)
    val num: Long = 0,
    val id: String,
    val title: String?,
    val author: String?,
    val num_comments: String,
    val name: String,
    val total_awards_received: Int?
) : Parcelable
