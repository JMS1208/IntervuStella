package com.capstone.Capstone2Project.network.service

import com.capstone.Capstone2Project.data.model.Topics
import com.capstone.Capstone2Project.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.*

interface MainService {

    @POST("membership/{user_uuid}")
    suspend fun postUserInfo(
        @Path("user_uuid") hostUUID: String,
        @Body userInfo: UserInfo
    ): Response<Int> //0:실패, 1:성공

    @PUT("user/interesting_field/{user_uuid}")
    suspend fun postInterestingField(
        @Path("user_uuid") hostUUID: String,
        @Body topics: Topics
    ): Response<Int> //0:실패, 1:성공
}