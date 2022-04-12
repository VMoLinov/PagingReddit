package ru.molinov.pagingreddit.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Data>):
            MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData.toString()
            }
        }
        return try {
            val response: List<ServerDataPage> = if (page.isEmpty()) {
                apiService.getData().data.children
            } else {
                apiService.getDataAfter(page, state.config.pageSize).data.children
            }
            val before = response.first().data.name
            val after = response.last().data.name
            val endOfList = response.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clearAll()
                    db.getDao().clearAll()
                }
                val keys = response.map {
                    RemoteKeys(
                        it.data.id,
                        before,
                        after
                    )
                }
                val list = mutableListOf<Data>()
                response.forEach { list.add(it.data) }
                db.remoteKeyDao().insertRemote(keys)
                db.getDao().insertAll(list)
            }
            MediatorResult.Success(endOfPaginationReached = endOfList)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, Data>):
            Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRefreshRemoteKey(state)
                remoteKeys?.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey ?: MediatorResult.Success(
                    endOfPaginationReached = false
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey ?: MediatorResult.Success(
                    endOfPaginationReached = true
                )
                nextKey
            }
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, Data>): RemoteKeys? {
        return withContext(Dispatchers.IO) {
            state.pages
                .firstOrNull { it.data.isNotEmpty() }
                ?.data?.firstOrNull()
                ?.let { data -> db.remoteKeyDao().getRemoteKeys(data.id) }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, Data>): RemoteKeys? {
        return withContext(Dispatchers.IO) {
            state.pages
                .lastOrNull { it.data.isNotEmpty() }
                ?.data?.lastOrNull()
                ?.let { data -> db.remoteKeyDao().getRemoteKeys(data.id) }
        }
    }

    private suspend fun getRefreshRemoteKey(state: PagingState<Int, Data>): RemoteKeys? {
        return withContext(Dispatchers.IO) {
            state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.id?.let { keysId ->
                    db.remoteKeyDao().getRemoteKeys(keysId)
                }
            }
        }
    }
}
