package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.common_ui.databinding.RvGridItemBinding
import by.ssrlab.data.data.common.RepositoryData
import coil.load
import coil.transform.RoundedCornersTransformation


class GridAdapter(
    private var entitiesList: List<RepositoryData>,
    private val navigateAction: (RepositoryData) -> Unit
): RecyclerView.Adapter<GridAdapter.GridHolder>() {

    inner class GridHolder(val binding: RvGridItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvGridItemBinding.inflate(
            inflater,
            parent,
            false
        )

        return GridHolder(binding)
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        holder.binding.apply {
            gridTitle.text = entitiesList[position].name
            gridPng.load(entitiesList[position].description?.logo) {
                transformations(RoundedCornersTransformation(16f))
                placeholder(by.ssrlab.common_ui.R.drawable.coil_placeholder)
                crossfade(500)
                crossfade(true)
            }
            gridRipple.setOnClickListener {
                navigateAction(entitiesList[position])
            }
        }
    }

    override fun getItemCount() = entitiesList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: List<RepositoryData>) {
        entitiesList = items
        notifyDataSetChanged()
    }
}