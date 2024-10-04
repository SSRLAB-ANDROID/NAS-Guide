package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.data.data.remote.DepartmentFilter
import by.ssrlab.ui.R

class FiltersAdapter(
    private var filters: List<DepartmentFilter>,
    private var selectedFilters: Set<DepartmentFilter>,
    private val onFilterSelected: (DepartmentFilter, Boolean) -> Unit
) : RecyclerView.Adapter<FiltersAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val filterName: TextView = view.findViewById(R.id.filter_name)
        private val filterCheckBox: CheckBox = view.findViewById(R.id.filter_checkbox)

        fun bind(filter: DepartmentFilter) {
            filterName.text = filter.keyName
            filterCheckBox.isChecked = selectedFilters.contains(filter)

            filterCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onFilterSelected(filter, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateFilters(newFilters: List<DepartmentFilter>, newSelectedFilters: Set<DepartmentFilter>) {
        filters = newFilters
        selectedFilters = newSelectedFilters
        notifyDataSetChanged()
    }
}
