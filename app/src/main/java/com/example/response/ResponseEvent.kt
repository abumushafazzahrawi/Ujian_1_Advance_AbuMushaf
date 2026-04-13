package com.example.response

import com.google.gson.annotations.SerializedName

data class ResponseEvent(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("listEvents")
    val listEvents: List<ListEventsItem>? = null,
    @SerializedName("event")
    val event: ListEventsItem? = null
)

data class ListEventsItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("imageLogo")
    val imageLogo: String,
    @SerializedName("mediaCover")
    val mediaCover: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("ownerName")
    val ownerName: String,
    @SerializedName("cityName")
    val cityName: String,
    @SerializedName("quota")
    val quota: Int,
    @SerializedName("registrants")
    val registrants: Int,
    @SerializedName("beginTime")
    val beginTime: String,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("link")
    val link: String
)
