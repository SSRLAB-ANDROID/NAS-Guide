package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.data.data.settings.remote.OrganizationLocale

class OrgSubsAdapter(
    private val subs: List<OrganizationLocale>,
    private val navigateAction: (List<String>) -> Unit
) : RecyclerView.Adapter<OrgSubsAdapter.SubsViewHolder>() {

    private var dataType: OrgSubs = OrgSubs.Achievements
    private var currentPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return SubsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubsViewHolder, position: Int) {
        setPosition(position)
        val item = subs[position]
        val data: List<String> = when (dataType) {
            OrgSubs.Achievements -> item.achievements
            OrgSubs.Contacts -> listOf(item.contacts)
            OrgSubs.ResearchAreas -> item.researchAreas?.filterNotNull() ?: listOf()
            OrgSubs.Laboratories -> TODO()
            OrgSubs.Departments -> listOf(item.description.departmentFilter.keyName)
        }
        holder.bind(data[0])
        holder.itemView.setOnClickListener {
            navigateAction(data)
        }
    }

    override fun getItemCount(): Int = subs.size

    private fun setPosition(position: Int) {
        currentPosition = position
    }

    fun navigateOrgSubs() {
        navigateAction(subs[0].achievements)
    }

    class SubsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(text: String) {
            (itemView as TextView).text = text
        }
    }
}