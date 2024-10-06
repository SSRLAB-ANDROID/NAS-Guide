package by.ssrlab.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.MainActivity
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentFiltersBinding
import by.ssrlab.ui.rv.FiltersAdapter
import by.ssrlab.ui.vm.FOrgsVM
import kotlinx.coroutines.flow.collectLatest

class FilterFragment : BaseFragment() {

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )

    override lateinit var fragmentViewModel: FOrgsVM

    private lateinit var binding: FragmentFiltersBinding
    private lateinit var filtersAdapter: FiltersAdapter

    override fun initBinding(container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_filters,
            container,
            false
        )
        return binding.root
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(inflater, container, false)
        fragmentViewModel = ViewModelProvider(requireActivity() as MainActivity)[FOrgsVM::class.java]
        binding.viewModel = fragmentViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupFiltersAdapter()
        observeFilters()

        return binding.root
    }

    private fun setupFiltersAdapter() {
        filtersAdapter = FiltersAdapter(
            filters = fragmentViewModel.availableFilters.value.keys.flatten(),
            selectedFilters = fragmentViewModel.selectedFilters.value,
            onFilterSelected = { filter, isSelected ->
                fragmentViewModel.onFilterSelected(filter, isSelected)
            }
        )

        binding.filtersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = filtersAdapter
        }
    }

    private fun observeFilters() {
        lifecycleScope.launchWhenStarted {
            fragmentViewModel.availableFilters.collectLatest { filtersMap ->
                val filtersList = filtersMap.keys.flatten().toList()
                filtersAdapter.updateFilters(filtersList, fragmentViewModel.selectedFilters.value)
            }
        }

        lifecycleScope.launchWhenStarted {
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