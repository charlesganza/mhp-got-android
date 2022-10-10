package app.mhp.got.networking

/**
 * class to keep track of pages since the API is not super friendly with paging information.
 * for example, no total item count is returned, making pagination a tad bit difficult
 * */
data class Pagination(var currentPage: Int, var loadingFirstTime: Boolean = true, var reachedLastPage: Boolean = false)