package app.mhp.got.ui.houses.usecase

import app.mhp.got.networking.APIService
import app.mhp.got.networking.RequestStatus
import app.mhp.got.ui.houses.model.House
import javax.inject.Inject

class GetHousesUseCase @Inject constructor(private val apiService: APIService) {

    suspend fun getHouses(page: Int): RequestStatus<List<House>, String> {
        return apiService.getHouses(page)
    }

}