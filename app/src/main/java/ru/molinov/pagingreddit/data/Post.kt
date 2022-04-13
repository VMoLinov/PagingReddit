package ru.molinov.pagingreddit.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Post(
    @PrimaryKey
    val id: String,
    val title: String?,
    val author: String?,
    val num_comments: String,
    val name: String,
    val total_awards_received: Int?
) : Parcelable

@Parcelize
data class ServerDataPage(
    val data: Post
) : Parcelable

@Parcelize
data class ServerDataChildren(
    val children: List<ServerDataPage>
) : Parcelable

@Parcelize
data class ServerData(
    val data: ServerDataChildren
) : Parcelable
