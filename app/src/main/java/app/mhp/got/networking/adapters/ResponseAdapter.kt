package app.mhp.got.networking.adapters

import app.mhp.got.networking.RequestStatus
import app.mhp.got.networking.NetworkUtils
import okhttp3.ResponseBody
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * request adapter
 * @param T: the success response type
 * @param U: the error type
 * */
class ResponseAdapter<T: Any, U: Any>(private val responseType: Type,
                                      private val errorBodyConverter: Converter<ResponseBody, U>,
                                      private val networkUtils: NetworkUtils?
) : CallAdapter<T, Call<RequestStatus<T, U>>> {

    override fun adapt(call: Call<T>): Call<RequestStatus<T, U>> {
       return CallDelegate(call, errorBodyConverter, responseType, networkUtils)
    }

    override fun responseType(): Type = responseType

}

class CallAdapterFactory(private val networkUtils: NetworkUtils?) : CallAdapter.Factory() {
    override fun get(responseType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (responseType !is ParameterizedType) {
            // raw types are not allowed, responseType must be parameterized
            return null
        }

        val containerType = getParameterUpperBound(0, responseType)
        if (getRawType(containerType) != RequestStatus::class.java) {
            return null
        }

        if (containerType !is ParameterizedType) {
            // raw types are not allowed, containerType must be parameterized
            return null
        }

        val (successType, errorBodyType) = containerType.getBodyTypes()
        val errorBodyConverter = retrofit.nextResponseBodyConverter<Any>(null, errorBodyType, annotations)

        return ResponseAdapter<Any, Any>(successType, errorBodyConverter, networkUtils)
    }

    //very useful method to get class parameterized types. ex: ResponseBody<Type1, Type2>
    private fun ParameterizedType.getBodyTypes(): Pair<Type, Type> {
        val successType = getParameterUpperBound(0, this)
        val errorType = getParameterUpperBound(1, this)
        return successType to errorType
    }

}
