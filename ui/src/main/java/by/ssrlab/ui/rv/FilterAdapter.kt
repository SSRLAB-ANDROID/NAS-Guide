package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.ui.databinding.RvFilterItemBinding

class FilterAdapter(
    private var entitiesMap: Map<String, Int>?,
    private val onFilterSelected: (Set<String>, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FilterItemHolder(val binding: RvFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvFilterItemBinding.inflate(inflater, parent, false)
        return FilterItemHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FilterItemHolder -> {
                holder.binding.apply {
                    entitiesMap?.entries?.toList()?.let { entries ->
                        val (filter, count) = entries[position]
                        rvSectionCheckbox.text = "$filter ($count)"

                        rvSectionCheckbox.setOnCheckedChangeListener(null)
                        rvSectionCheckbox.isChecked = false
                        rvSectionCheckbox.setOnCheckedChangeListener { _, isChecked ->
//                                onFilterSelected(filter, isChecked)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount() = entitiesMap?.size ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: Map<String, Int>) {
        entitiesMap = items
        notifyDataSetChanged()
    }
}