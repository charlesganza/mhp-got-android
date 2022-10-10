package app.mhp.got.ui.houses.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.mhp.got.R
import app.mhp.got.ui.houses.model.House
import app.mhp.got.ui.utils.makeBold

class HousesListAdapter(val context: Context, val houseList: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var houseClicked: ItemClicked<House>
    private var isLoading = false
    private lateinit var loadMoreItems: LoadMoreItems
    object LoadingViewType
    object EndOfListViewType

    inner class HouseView(view: View) : RecyclerView.ViewHolder(view) {
        val houseName: TextView = view.findViewById(R.id.houseName)
        val description: TextView = view.findViewById(R.id.houseDescription)
    }

    inner class LoadingView(view: View) : RecyclerView.ViewHolder(view) {
        val loadingView: TextView = view.findViewById(R.id.loadingText)
    }

    inner class EndOfListView(view: View) : RecyclerView.ViewHolder(view) {
        val endOfListView: TextView = view.findViewById(R.id.endOfListView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ViewType.VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(context).inflate(R.layout.house_list_item, parent, false)
                parent.removeView(view)
                HouseView(view)
            }
            ViewType.VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(context).inflate(R.layout.loading_layout, parent, false)
                parent.removeView(view)
                LoadingView(view)
            }
            ViewType.VIEW_TYPE_END_OF_LIST -> {
                val view = LayoutInflater.from(context).inflate(R.layout.end_of_list_view, parent, false)
                parent.removeView(view)
                EndOfListView(view)
            }
            else -> {
                throw IllegalStateException("Unknown view")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(houseList[position]){
            is LoadingViewType -> {
                ViewType.VIEW_TYPE_LOADING
            }
            is House -> {
                ViewType.VIEW_TYPE_ITEM
            }
            is EndOfListViewType -> {
                ViewType.VIEW_TYPE_END_OF_LIST
            }
            else -> {
                throw IllegalStateException("Unknown view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HouseView -> {
                context.makeBold(holder.houseName)

                val house = houseList[position] as House

                holder.houseName.text = house.name
                holder.description.text = house.region

                holder.itemView.setOnClickListener {
                    houseClicked.onItemClicked(house)
                }
            }
            is LoadingView -> {
                context.makeBold(holder.loadingView)
            }
            is EndOfListView -> {
                context.makeBold(holder.endOfListView)
            }
        }
    }

    fun setHouses(houses: List<House>){
        houseList.addAll(houses)
        notifyDataSetChanged()
    }

    fun addHouses(houses: List<House>){
        val startIndex = houseList.size + 1

        houseList.addAll(houses)
        notifyItemRangeInserted(startIndex, houses.size)

        Log.e("called", "${houses.size}")
    }

    fun clear(){
        val size = houseList.size
        houseList.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addBottomProgressBar(){
        if(houseList.none { it is LoadingViewType }) {
            setLoadingStatus(loading = true)
            houseList.add(LoadingViewType)
            notifyItemInserted(houseList.size - 1)
            recyclerView.scrollToPosition(houseList.size - 1)
        }
    }

    fun removeProgressBar(){
        setLoadingStatus(loading = false)
        houseList.removeAll { it is LoadingViewType }
        notifyItemRemoved(houseList.size - 1)
    }

    fun addEndOfListView(){
        if(houseList.none { it is EndOfListViewType }) {
            houseList.add(EndOfListViewType)
            recyclerView.post {
                notifyItemInserted(houseList.size - 1)
            }
        }
    }

    fun removeEndOfListView(){
        houseList.removeAll { it is EndOfListViewType }
        notifyItemRemoved(houseList.size - 1)
    }

    private fun setLoadingStatus(loading: Boolean) {
        isLoading = loading
    }

    val isLoadingMore: Boolean get() = isLoading

    fun setRecyclerView(_recyclerView: RecyclerView){
        recyclerView = _recyclerView
    }

    fun getClickedHouse(_houseClicked: ItemClicked<House>){
        houseClicked = _houseClicked
    }

    fun loadMoreListener(_loadMoreItems: LoadMoreItems){
        loadMoreItems = _loadMoreItems

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!isLoading && !recyclerView.canScrollVertically(1)){
                    loadMoreItems.onLoadMore()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return houseList.size
    }

}

object ViewType {
    const val VIEW_TYPE_LOADING = 1
    const val VIEW_TYPE_ITEM = 2
    const val VIEW_TYPE_END_OF_LIST = 3
}

interface ItemClicked<T> {
    fun onItemClicked(item: T)
}

interface LoadMoreItems {
    fun onLoadMore()
}