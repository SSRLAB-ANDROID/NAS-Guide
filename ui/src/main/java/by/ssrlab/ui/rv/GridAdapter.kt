package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.common_ui.databinding.RvGridItemBinding
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.ErrorItemBinding
import coil.load
import coil.transform.RoundedCornersTransformation

class GridAdapter(
    private var entitiesList: List<RepositoryData>?,
    private val navigateAction: (RepositoryData) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
        private const val VIEW_TYPE_ERROR = 2
    }

    private var isLoading = false
    private var errorMessage: String? = null

    inner class GridHolder(val binding: RvGridItemBinding) : RecyclerView.ViewHolder(binding.root)

    inner class LoadingHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class ErrorHolder(val binding: ErrorItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> VIEW_TYPE_LOADING
            errorMessage != null -> VIEW_TYPE_ERROR
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = inflater.inflate(R.layout.item_loading, parent, false)
                LoadingHolder(view)
            }

            VIEW_TYPE_ERROR -> {
                val binding = ErrorItemBinding.inflate(inflater, parent, false)
                ErrorHolder(binding)
            }

            else -> {
                val binding = RvGridItemBinding.inflate(inflater, parent, false)
                GridHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GridHolder -> holder.binding.apply {
                gridTitle.text = entitiesList!![position].name
                gridPng.load(entitiesList!![position].description?.logo) {
                    transformations(RoundedCornersTransformation(16f))
                    placeholder(by.ssrlab.common_ui.R.drawable.coil_placeholder)
                    crossfade(500)
                    crossfade(true)
                }
                gridRipple.setOnClickListener {
                    navigateAction(entitiesList!![position])
                }
            }
            is LoadingHolder -> { /*auto*/ }
            is ErrorHolder -> { /*auto*/ }
        }
    }

    override fun getItemCount() =
        if (isLoading || errorMessage != null) 1 else entitiesList?.let { entitiesList!!.size } ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: List<RepositoryData>) {
        isLoading = false
        errorMessage = null
        entitiesList = items
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showLoading() {
        isLoading = true
        errorMessage = null
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showError(message: String) {
        errorMessage = message
        isLoading = false
        notifyDataSetChanged()
    }
}