package app.mhp.got.ui.houses.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.mhp.got.databinding.HouseListFragmentBinding
import app.mhp.got.networking.RequestStatus
import app.mhp.got.ui.houses.list.HousesListAdapter
import app.mhp.got.ui.houses.list.ItemClicked
import app.mhp.got.ui.houses.list.LoadMoreItems
import app.mhp.got.ui.houses.model.House
import app.mhp.got.ui.houses.viewmodel.HouseViewModel
import app.mhp.got.ui.utils.*
import dagger.hilt.android.AndroidEntryPoint
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HouseList : Fragment() {

    private val houseViewModel: HouseViewModel by viewModels()
    private var _binding: HouseListFragmentBinding? = null
    private val binding get() = _binding!!
    private val housesListAdapter: HousesListAdapter by lazy { HousesListAdapter(requireActivity(), mutableListOf()) }

    override fun onAttach(context: Context) {
        super.onAttach(ViewPumpContextWrapper.wrap(context))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HouseListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHousesList()

        binding.refreshHouses.setOnRefreshListener {
            housesListAdapter.clear()
            houseViewModel.getHouses(refresh = true)
        }

        lifecycleScope.launch {
            houseViewModel.housesResponse.collect { response ->
                binding.noInformationFound.hideView()
                housesListAdapter.removeProgressBar()
                housesListAdapter.removeEndOfListView()
                binding.refreshHouses.isRefreshing = false
                binding.loadingGroup.hideView()

                when(response){
                    is RequestStatus.Loading -> {
                        if(houseViewModel.pagination.loadingFirstTime){
                            //loading first page, show initial progress bar
                            binding.loadingGroup.showView()
                        } else {
                            //the bottom loading bar will be shown when loading additional pages
                            housesListAdapter.addBottomProgressBar()
                        }
                    }
                    is RequestStatus.Success -> {
                        if(houseViewModel.pagination.loadingFirstTime){
                            housesListAdapter.setHouses(response.data)
                        } else {
                            housesListAdapter.addHouses(response.data)
                        }
                        houseViewModel.pagination.loadingFirstTime = false
                    }
                    else -> {
                        if(housesListAdapter.houseList.isEmpty()) binding.noInformationFound.showView()
                        //you can also handle individual error types here: Server, API, parse errors...
                        binding.root.showErrorResponse(response)
                    }
                }
            }
        }

    }

    private fun initHousesList(){
        binding.housesList.layoutManager = LinearLayoutManager(requireActivity())
        binding.housesList.setHasFixedSize(true)
        binding.housesList.adapter = housesListAdapter
        housesListAdapter.setRecyclerView(binding.housesList)

        //listen for clicked house
        housesListAdapter.getClickedHouse(object: ItemClicked<House> {
            override fun onItemClicked(house: House) {
                safeNavigation(HouseListDirections.actionHouseListToDetail(house))
            }
        })

        housesListAdapter.loadMoreListener(object: LoadMoreItems {
            override fun onLoadMore() {
                if(!housesListAdapter.isLoadingMore) houseViewModel.getHouses()
                if(houseViewModel.pagination.reachedLastPage) housesListAdapter.addEndOfListView()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}