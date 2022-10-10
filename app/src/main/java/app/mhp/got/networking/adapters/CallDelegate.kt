package app.mhp.got.networking.adapters

import app.mhp.got.BuildConfig
import app.mhp.got.networking.RequestStatus
import app.mhp.got.networking.NetworkUtils
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import okhttp3.Request
import okhttp3.ResponseBody
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.lang.reflect.Type

/**
 * @[Call] delegate call where we perform some modification to the call before returning it
 * @param T is the success type
 * @param S is the error type
 * this class allows us to handle all network request state operations in one place. We only receive a clean @[RequestStatus] class with wrapped response data :)
 * */
class CallDelegate<T : Any, S : Any>(
    private val delegate: Call<T>,
    private val errorBodyConverter: Converter<ResponseBody, S>,
    private val successType: Type,
    private val networkUtils: NetworkUtils?
) : Call<RequestStatus<T, S>> {

    override fun enqueue(callback: Callback<RequestStatus<T, S>>) = synchronized(this) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val statusCode = response.code()
                val body = response.body()
                val errorBody = response.errorBody()

                val requestStatus = if(response.isSuccessful && body != null){
                    RequestStatus.Success(statusCode = statusCode, data = body)
                } else {
                    if(statusCode >= 500){
                        RequestStatus.ServerError
                    } else {
                        RequestStatus.ApiError(statusCode = statusCode, errorMessage = errorBodyConverter.getError(errorBody))
                    }
                }
                callback.onResponse(this@CallDelegate, Response.success(requestStatus))
            }
            override fun onFailure(call: Call<T>, throwable: Throwable) {
                if(BuildConfig.DEBUG) throwable.printStackTrace()

                val requestStatus = when(throwable) {
                    is IOException -> {
                        if(networkUtils?.isConnected == true) {
                            RequestStatus.ServerUnreachable
                        } else RequestStatus.NetworkError
                    }
                    is JsonDataException, is JsonEncodingException -> {
                        RequestStatus.ParseError
                    }
                    else -> {
                        RequestStatus.UnknownError
                    }
                }
                callback.onResponse(this@CallDelegate, Response.success(requestStatus))
            }
        })
    }

    private fun <S> Converter<ResponseBody, S>.getError(responseBody: ResponseBody?): S? {
        responseBody ?: return null
        return try {
            this.convert(responseBody)
        } catch (e: Exception) {
            null
        }
    }

    override fun isExecuted(): Boolean = synchronized(this) {
        delegate.isExecuted
    }

    override fun clone(): Call<RequestStatus<T, S>> = CallDelegate(
        delegate.clone(),
        errorBodyConverter,
        successType,
        networkUtils)

    override fun isCanceled(): Boolean = synchronized(this) {
        delegate.isCanceled
    }

    override fun cancel() = synchronized(this) {
        delegate.cancel()
    }

    override fun execute(): Response<RequestStatus<T, S>> {
        throw UnsupportedOperationException("Synchronous operation not allowed")
    }

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}