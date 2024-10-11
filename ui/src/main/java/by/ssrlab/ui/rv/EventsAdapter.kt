package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import by.ssrlab.common_ui.databinding.RvEventsItemBinding
import by.ssrlab.common_ui.databinding.RvEventsTitleBinding
import by.ssrlab.data.data.settings.remote.EventLocale
import by.ssrlab.domain.utils.fromHtml
import coil.load
import coil.transform.RoundedCornersTransformation

private const val ITEM_TITLE = 0
private const val ITEM_DATE = 1

class EventsAdapter(
    private var entitiesList: List<EventLocale>,
    private val title: String,
) : RecyclerView.Adapter<EventsAdapter.EventsHolder>() {

    inner class EventsHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            if (viewType == ITEM_TITLE) RvEventsTitleBinding.inflate(inflater, parent, false)
            else RvEventsItemBinding.inflate(inflater, parent, false)

        return EventsHolder(binding)
    }

    override fun onBindViewHolder(holder: EventsHolder, position: Int) {
        holder.binding.apply {
            when (this@apply) {
                is RvEventsTitleBinding -> {
                    this.rvDatesTitle.text = title
                }

                is RvEventsItemBinding -> {
                    this.rvEventsDate.text = entitiesList[position - 1].name
                    this.rvEventsBody.text = entitiesList[position - 1].about.fromHtml()

                    this.rvEventsPng.load(entitiesList[position - 1]) {
                        crossfade(true)
                        crossfade(500)
                        placeholder(by.ssrlab.common_ui.R.drawable.coil_placeholder)
                        transformations(RoundedCornersTransformation(8f))
                    }

                    this.rvEventsRipple.setOnClickListener {
                        //TODO
                    }
                }
            }
        }
    }

    override fun getItemCount() = entitiesList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ITEM_TITLE
        else ITEM_DATE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: List<EventLocale>, date: String) {
        entitiesList = items.filter { it.event.startDate.drop(5) == date }
        notifyDataSetChanged()
    }
}