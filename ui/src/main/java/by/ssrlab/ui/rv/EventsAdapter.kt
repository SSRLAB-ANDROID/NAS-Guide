package by.ssrlab.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import by.ssrlab.common_ui.databinding.RvEventsItemBinding
import by.ssrlab.common_ui.databinding.RvEventsTitleBinding
import by.ssrlab.data.data.settings.remote.EventLocale
import by.ssrlab.domain.utils.fromHtml
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.ErrorItemBinding


class EventsAdapter(
    private var entitiesList: List<EventLocale>,
    private var startTitle: String,
    private val changedTitle: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM_TITLE = 0
        private const val VIEW_TYPE_ITEM_DATE = 1
        private const val VIEW_TYPE_LOADING = 2
        private const val VIEW_TYPE_ERROR = 3
    }

    private var title = startTitle
    private var isLoading = false
    private var errorMessage: String? = null

    inner class EventsHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    inner class LoadingHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class ErrorHolder(val binding: ErrorItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> VIEW_TYPE_LOADING
            errorMessage != null -> VIEW_TYPE_ERROR
            position == 0 -> VIEW_TYPE_ITEM_TITLE
            else -> VIEW_TYPE_ITEM_DATE
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

            VIEW_TYPE_ITEM_TITLE -> {
                val binding = RvEventsTitleBinding.inflate(inflater, parent, false)
                return EventsHolder(binding)
            }

            VIEW_TYPE_ITEM_DATE -> {
                val binding = RvEventsItemBinding.inflate(inflater, parent, false)
                return EventsHolder(binding)
            }

            else -> {
                val binding = RvEventsItemBinding.inflate(inflater, parent, false)
                return EventsHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventsHolder -> holder.binding.apply {
                when (this@apply) {
                    is RvEventsTitleBinding -> {
                        this.rvDatesTitle.text = title
                    }

                    is RvEventsItemBinding -> {
                        this.rvEventsDate.text = entitiesList[position - 1].name
                        this.rvEventsBody.text = entitiesList[position - 1].about.fromHtml()

//                        this.rvEventsPng.load(entitiesList[position - 1]) {
//                            crossfade(true)
//                            crossfade(500)
//                            placeholder(by.ssrlab.common_ui.R.drawable.coil_placeholder)
//                            transformations(RoundedCornersTransformation(8f))
//                        }

                        this.rvEventsRipple.setOnClickListener {}
                    }
                }
            }

            is ErrorHolder -> {}
            is LoadingHolder -> {}
        }
    }

    override fun getItemCount() =
        if (isLoading || errorMessage != null) 1 else entitiesList.let { entitiesList.size + 1 }


    private fun findNearestEventDate(dates: List<EventLocale>, date: String): String? {
        val futureDates = dates.map { it.event.startDate.drop(5) }.filter { it > date }
        return futureDates.minOrNull()
    }

    private fun filteredListIsEmpty(items: List<EventLocale>, date: String) {
        try {
            entitiesList = items.filter {
                it.event.startDate.drop(5) ==
                        findNearestEventDate(items, date)
            }
        } catch (e: Throwable) {
            showError(e.message.toString())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: List<EventLocale>, date: String) {
        isLoading = false
        errorMessage = null
        entitiesList = items.filter { it.event.startDate.drop(5) == date }

        if (entitiesList.isEmpty()) {
            filteredListIsEmpty(items, date)
            title = changedTitle
        } else {
            title = startTitle
        }
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