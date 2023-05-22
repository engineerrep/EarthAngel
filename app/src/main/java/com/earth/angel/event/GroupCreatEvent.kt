package com.earth.angel.event

data class GroupCreatEvent(
    var isConcern: Boolean?=null,
    var userId: Long?=null,
    var isLike: Boolean?=null,
)
