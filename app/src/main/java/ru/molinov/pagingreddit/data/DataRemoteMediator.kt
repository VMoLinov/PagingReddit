package ru.molinov.pagingreddit.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.molinov.pagingreddit.network.ApiService
import java.io.IOException

@ExperimentalPagingApi
class DataRemoteMediator constructor(
    private val db: Database,
    private val apiService: ApiService
) : RemoteMediator<Int, Data>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Data>): MediatorResult {
        return try {
            when (loadType) {
                LoadType.REFRESH -> handleLoad(null, state.config.pageSize, true)
                LoadType.PREPEND -> MediatorResult.Success(true)
                LoadType.APPEND -> {
                    val key = state.lastItemOrNull()?.name
                    handleLoad(key, state.config.pageSize, false)
                }
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun handleLoad(
        key: String?,
        pageSize: Int,
        clear: Boolean
    ): MediatorResult {
        val response: List<ServerDataPage> =
            if (key == null) apiService.getData().data.children
            else apiService.getDataAfter(key, pageSize).data.children
        val isEndList = response.isEmpty()
        val list = mutableListOf<Data>()
        response.forEach { serverData -> list.add(serverData.data) }
        db.withTransaction {
            if (clear) db.getDao().clearAll()
            db.getDao().insertAll(list)
        }
        return MediatorResult.Success(isEndList)
    }
}
