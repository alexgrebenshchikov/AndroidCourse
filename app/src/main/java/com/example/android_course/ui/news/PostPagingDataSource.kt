package com.example.android_course.ui.news

import androidx.paging.PagingSource
import com.example.android_course.data.network.Api
import com.example.android_course.entity.Post
import timber.log.Timber

class PostPagingDataSource(private val service: Api) :
    PagingSource<String, Post>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        val from = params.key ?: "now"
        return try {
            val response = service.loadPosts(from, "10")

            val data = response.posts

            var nextFrom: String? = null
            if (response.next) {

                nextFrom = data.lastOrNull()?.updatedAt
            }

            LoadResult.Page(
                data = data.orEmpty(),
                prevKey = null,
                nextKey = nextFrom
            )
        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }
}