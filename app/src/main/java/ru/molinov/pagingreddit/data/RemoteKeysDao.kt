package ru.molinov.pagingreddit.data

import androidx.room.*
import androidx.room.Dao

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRemote(list: List<RemoteKeys>)

    @Query("SELECT * FROM remoteKey WHERE repoId = :id")
    fun getRemoteKeys(id: String): RemoteKeys

    @Query("DELETE FROM remoteKey")
    fun clearAll()
}

@Entity(tableName = "remoteKey")
data class RemoteKeys(
    @PrimaryKey
    val repoId: String,
    val prevKey: String?,
    val nextKey: String?
)
