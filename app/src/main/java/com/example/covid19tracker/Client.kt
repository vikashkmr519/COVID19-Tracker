package com.example.covid19tracker

import okhttp3.OkHttpClient
import okhttp3.Request

object Client {

    private val OkhttpClient = OkHttpClient()

    private val request = Request.Builder()
        .url("https://api.covid19india.org/data.json")
        .build()

    val api = OkhttpClient.newCall(request)
}