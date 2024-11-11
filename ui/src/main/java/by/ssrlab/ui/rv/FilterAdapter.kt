package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.data.data.remote.DepartmentFilter
import by.ssrlab.ui.databinding.RvFilterItemBinding

class FilterAdapter(
    private var entitiesMap: Map<Set<DepartmentFilter>, Int>?,
    private val onFilterSelected: (Set<DepartmentFilter>, Boolean) -> Unit
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
                    entitiesMap?.entries?.toList()?.get(position)?.let { (filterSet, count) ->
                        rvSectionTitle.text = "Filter: ${filterSet.joinToString(", ") { it.keyName }}"
                        rvSectionCategories.text = "Count: $count"
                    }
                }
            }
        }
    }

    override fun getItemCount() = entitiesMap?.size ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: Map<Set<DepartmentFilter>, Int>) {
        entitiesMap = items
        notifyDataSetChanged()
    }
}