package com.midburn.gate.midburngate.contractors

import retrofit2.Call
import retrofit2.http.GET

interface GitHubService {

    @GET("orgs/octokit/repos")
    fun testGet(): Call<List<TestObj>>
}