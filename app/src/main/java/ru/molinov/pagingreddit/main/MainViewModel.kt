package ru.molinov.pagingreddit.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.molinov.pagingreddit.data.DataRemoteMediator
import ru.molinov.pagingreddit.data.Database
import ru.molinov.pagingreddit.data.Post
import ru.molinov.pagingreddit.network.ApiService
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val db: Database,
    private val apiService: ApiService
) : ViewModel() {

    @ExperimentalPagingApi
    fun getAll(): Flow<PagingData<Post>> = Pager(
        config = PagingConfig(
            200,
            enablePlaceholders = false,
            prefetchDistance = 100,
            maxSize = 10000
        ),
        pagingSourceFactory = { db.getDao().getData() },
        remoteMediator = DataRemoteMediator(db, apiService)
    ).flow.cachedIn(viewModelScope)
}
