package com.earth.libbase.entity

data class SearchEntity (
    var endRow: Int,
    var firstPage: Int,
    var hasNextPage: Boolean,
    var hasPreviousPage: Boolean,
    var isFirstPage: Boolean,
    var isLastPage: Boolean,
    var lastPage: Int,
    var list: List<SearchDetailEntity>,
    var navigateFirstPage: Int,
    var navigateLastPage: Int,
    var navigatePages: Int,
    var navigatepageNums: List<Int>,
    var nextPage: Int,
    var pageNum: Int,
    var pageSize: Int,
    var pages: Int,
    var prePage: Int,
    var size: Int,
    var startRow: Int,
    var total: Int,
        )