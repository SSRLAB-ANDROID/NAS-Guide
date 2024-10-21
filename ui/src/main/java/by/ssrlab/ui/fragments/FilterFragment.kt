package by.ssrlab.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.ssrlab.ui.databinding.FragmentFiltersBinding
import by.ssrlab.ui.rv.FiltersAdapter
import by.ssrlab.ui.vm.FOrgsVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFiltersBinding
    private lateinit var fragmentViewModel: FOrgsVM
    private lateinit var filtersAdapter: FiltersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFiltersAdapter()
        observeFilters()
        fragmentViewModel = TODO()
    }

    private fun setupFiltersAdapter() {
        filtersAdapter = FiltersAdapter(
            filters = fragmentViewModel.availableFilters.value.keys.flatten(),
            selectedFilters = fragmentViewModel.selectedFilters.value,
            onFilterSelected = { filter, isSelected ->
                fragmentViewModel.onFilterSelected(filter, isSelected)
            }
        )
    }

    private fun observeFilters() {
        lifecycleScope.launch {
            fragmentViewModel.availableFilters.collectLatest { filtersMap ->
                val filtersList = filtersMap.keys.flatten().toList()
                filtersAdapter.updateFilters(filtersList, fragmentViewModel.selectedFilters.value)
            }
        }

        lifecycleScope.launch {
            fragmentViewModel.selectedFilters.collectLatest { selectedFilters ->
                filtersAdapter.updateFilters(
                    fragmentViewModel.availableFilters.value.keys.flatten(),
                    selectedFilters
                )
            }
        }
    }

    fun applyFilters(view: View) {
        fragmentViewModel.applyFilters()
    }
}