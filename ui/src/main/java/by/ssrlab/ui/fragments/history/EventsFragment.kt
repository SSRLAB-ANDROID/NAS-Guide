package by.ssrlab.ui.fragments.history

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.data.util.MainActivityUiState
import by.ssrlab.data.util.ToolbarStateByDates
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.MainActivity
import by.ssrlab.ui.databinding.FragmentEventsBinding
import by.ssrlab.ui.rv.EventsAdapter
import by.ssrlab.ui.vm.FDatesVM
import org.koin.androidx.viewmodel.ext.android.viewModel

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
}
