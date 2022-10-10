package app.mhp.got.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlin.math.ceil

fun String?.emptyStringHandler(): String {
    return if(this == null || this == "null" || this == "") "N/A" else this
}

inline fun <reified T> jsonDataAdapter(): JsonAdapter<T> = Moshi.Builder().build().adapter(T::class.java)

inline fun <reified T> toJson(data: T): String {
    return jsonDataAdapter<T>().toJson(data)
}

inline fun <reified T> fromJson(json: String): T? {
    return try {
        jsonDataAdapter<T>().fromJson(json)
    } catch (e: Exception){
        null
    }
}

fun getTotalPages(count: Int, pageLimit: Int): Int {
    val pageModulus = count % pageLimit

    return if(pageModulus == 0){
        count / pageLimit
    } else {
        ceil(count.toDouble() / pageLimit).toInt()
    }
}
