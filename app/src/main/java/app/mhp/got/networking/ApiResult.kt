package app.mhp.got.networking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ApiResult<T>(@Json(name = "count") val count: Int = 0, @Json(name = "results") val results: List<T>): Serializable