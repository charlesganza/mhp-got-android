package app.mhp.got.networking

import app.mhp.got.ui.houses.model.House
import retrofit2.http.*

interface APIService {

    @GET(Endpoints.ENDPOINT_HOUSES)
    suspend fun getHouses(@Query("page") page: Int, @Query("pageSize") pageSize: Int = 20): RequestStatus<List<House>, String>

    object Endpoints {
        const val ENDPOINT_HOUSES = "houses"
    }

}