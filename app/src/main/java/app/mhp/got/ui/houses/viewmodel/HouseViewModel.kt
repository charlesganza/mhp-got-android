package app.mhp.got.ui.houses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.mhp.got.networking.Pagination
import app.mhp.got.networking.RequestStatus
import app.mhp.got.ui.houses.model.House
import app.mhp.got.ui.houses.usecase.GetHousesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseViewModel @Inject constructor(private val getHousesUseCase: GetHousesUseCase) : ViewModel() {

    private val _housesResponse = MutableSharedFlow<RequestStatus<List<House>, String>>()
    val housesResponse get() = _housesResponse

    private var _pagination = Pagination(currentPage = 1)
    val pagination get() = _pagination

    init {
        getHouses()
    }

    fun getHouses(refresh: Boolean = false) {
        if(refresh){
            //fetch from scratch
            _pagination = Pagination(currentPage = 1)
        }

        if(_pagination.reachedLastPage) return

        kotlin.runCatching {
            viewModelScope.launch(Dispatchers.IO) {
                _housesResponse.emit(RequestStatus.Loading)

                val response = getHousesUseCase.getHouses(_pagination.currentPage)

                _housesResponse.emit(response)

                if(response is RequestStatus.Success){
                    //increment current page
                    _pagination.currentPage += 1
                    //if array is empty then we've reached the end
                    _pagination.reachedLastPage = response.data.isEmpty()
                }
            }
        }.onFailure {
            viewModelScope.launch(Dispatchers.IO) {
                _housesResponse.emit(RequestStatus.UnknownError)
            }
        }
    }

}
