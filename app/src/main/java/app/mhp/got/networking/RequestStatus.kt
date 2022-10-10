package app.mhp.got.networking

/**
 * request status sealed class
 * carries the response data to represent the request state
 * */
sealed class RequestStatus<out T: Any, out S: Any> {

    object Loading : RequestStatus<Nothing, Nothing>()

    data class Success<T: Any>(val statusCode: Int = 0,
                               val data: T): RequestStatus<T, Nothing>()

    data class ApiError<S: Any>(val statusCode: Int = 0,
                                val errorMessage: S?): RequestStatus<Nothing, S>()

    data class Cached<T: Any>(val cachedData: T): RequestStatus<T, Nothing>()

    object ServerUnreachable : RequestStatus<Nothing, Nothing>()
    object ServerError : RequestStatus<Nothing, Nothing>()
    object ParseError : RequestStatus<Nothing, Nothing>()
    object NetworkError : RequestStatus<Nothing, Nothing>()
    object UnknownError : RequestStatus<Nothing, Nothing>()
}