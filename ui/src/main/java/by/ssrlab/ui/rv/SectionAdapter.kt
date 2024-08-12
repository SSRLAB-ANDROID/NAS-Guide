package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.common_ui.databinding.RvSectionItemBinding
import by.ssrlab.data.data.common.RepositoryData
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation

class SectionAdapter(
    private var entitiesList: List<RepositoryData>,
    private val navigateAction: (RepositoryData) -> Unit
): RecyclerView.Adapter<SectionAdapter.DevelopmentsHolder>() {

    inner class DevelopmentsHolder(val binding: RvSectionItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevelopmentsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvSectionItemBinding.inflate(
            inflater,
            parent,
            false
        )

        return DevelopmentsHolder(binding)
    }

    override fun onBindViewHolder(holder: DevelopmentsHolder, position: Int) {
        holder.binding.apply {
            val positionStr = "${position+1}."
            rvSectionNumber.text = positionStr
            rvSectionTitle.text = entitiesList[position].name
            rvSectionPng.load(entitiesList[position].description?.logo) {
                size(210, 130)
                scale(Scale.FIT)
                transformations(RoundedCornersTransformation(16f))
                placeholder(by.ssrlab.common_ui.R.drawable.coil_placeholder)
                crossfade(500)
                crossfade(true)
            }
            rvSectionRipple.setOnClickListener {
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