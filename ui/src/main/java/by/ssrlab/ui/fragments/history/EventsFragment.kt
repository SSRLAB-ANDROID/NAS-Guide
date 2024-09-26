package by.ssrlab.ui.fragments.history

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.pm.ShortcutManagerCompat.ShortcutMatchFlags
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.settings.remote.EventLocale
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.data.util.MainActivityUiState
import by.ssrlab.data.util.ToolbarStateByDates
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.MainActivity
import by.ssrlab.ui.databinding.FragmentEventsBinding
import by.ssrlab.ui.rv.EventsAdapter
import by.ssrlab.ui.vm.FDatesVM
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsFragment : BaseFragment() {

    private lateinit var binding: FragmentEventsBinding
    private lateinit var adapter: EventsAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = false,
        isDates = true
    )

    override val fragmentViewModel: FDatesVM by viewModel()

    override fun onStart() {
        super.onStart()

        (requireActivity() as MainActivity).apply {
            changeLayoutState(MainActivityUiState.EventFragment)
            setupToolbarByDates(ToolbarStateByDates.OnCreate)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_events)
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
        }

        initAdapter()
        observeOnDataChanged()
        observeOnDateChanged()
    }

    override fun onStop() {
        super.onStop()

        (requireActivity() as MainActivity).apply {
            changeLayoutState(MainActivityUiState.Other)
            setupToolbarByDates(ToolbarStateByDates.OnDestroy)
        }
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.apply {
            datesObservableBoolean.observe(viewLifecycleOwner) {
                updateEventsList()
                adapter.updateData(datesData.value!!, activityVM.currentDateNumeric.value!!)
            }
        }
    }

    private fun observeOnDateChanged() {
        activityVM.currentDateNumeric.observe(viewLifecycleOwner) {
            if (fragmentViewModel.datesObservableBoolean.value == true) {
                adapter.updateData(fragmentViewModel.datesData.value!!, it)
            }
        }
    }

    override fun initAdapter() {
        adapter = EventsAdapter(
            fragmentViewModel.datesData.value!!,
            requireContext().resources.getString(by.ssrlab.common_ui.R.string.on_this_day)
        )

        binding.apply {
            eventsRv.layoutManager = LinearLayoutManager(requireContext())
            eventsRv.adapter = adapter
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding = FragmentEventsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }

    private fun updateEventsList() {
        val updatedEvents = mutableListOf<EventLocale>()

        fragmentViewModel.datesData.value?.let { events ->
            for (event in events) {
                val updatedEvent = updateEventName(event)
                updatedEvents.add(updatedEvent)
            }
        }

        fragmentViewModel.updateEvents(updatedEvents)
    }

    private fun updateEventName(event: EventLocale): EventLocale {
        val formattedName = formatEventName(event.name)
        return event.copy(name = formattedName)
    }

    private fun formatEventName(dateString: String): String {
        try {
            val correctedWithDash = dateString.replace("–", "-")
            val correctedWithDot = correctedWithDash.replace(".", "-")
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = sdf.parse(correctedWithDot) ?: return ""

            val calendar = Calendar.getInstance()
            calendar.time = date
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            return "$day ${formatMonth(month)} $year"

        } catch (e: Throwable) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            return dateString
        }
    }

    private fun formatMonth(month: Int): String {
        return when (month) {
            0 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.january)
            1 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.february)
            2 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.march)
            3 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.april)
            4 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.may)
            5 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.june)
            6 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.july)
            7 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.august)
            8 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.september)
            9 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.october)
            10 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.november)
            11 -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.december)
            else -> requireContext().resources.getString(by.ssrlab.common_ui.R.string.january)
        }
    }
}
