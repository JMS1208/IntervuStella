package com.capstone.Capstone2Project.ui.screen.comment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.network.service.MainService

class CommentPagingSource(
    private val service: MainService,
    private val questionUUID: String
): PagingSource<Int, TodayQuestionComment>() {
    override fun getRefreshKey(state: PagingState<Int, TodayQuestionComment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) // 그주위의 페이지를 읽어오는 역할
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodayQuestionComment> {
        return try {
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            val response = service.getTodayQuestionCommentList(questionUUID = questionUUID, page = pageNumber, perPage = params.loadSize)

            val endOfPaginationReached = response.body().isNullOrEmpty()

            val comments = response.body() ?: emptyList()

            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (endOfPaginationReached) {
                null
            } else {
                pageNumber + (params.loadSize/ PAGING_SIZE)
            }

            LoadResult.Page(
                data = comments,
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    companion object { // 키의 초깃값이 null 이라서 시작페이지를 1로 지정해줌
        const val STARTING_PAGE_INDEX = 1
        const val PAGING_SIZE = 20
    }
}