package by.ssrlab.ui.fragments.inventions

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.MainActivity
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentInventionFiltersBinding
import by.ssrlab.ui.rv.FilterAdapter
import by.ssrlab.ui.vm.FDevelopmentsVM
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class InventionsFilterFragment : BaseFragment() {

    private lateinit var binding: FragmentInventionFiltersBinding
    private lateinit var adapter: FilterAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FDevelopmentsVM by activityViewModel<FDevelopmentsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityVM.apply {
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
        }

        binding.apply {
            viewModel = this@InventionsFilterFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
        observeOnDataChanged()
        disableButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val searchButton: ImageButton = requireActivity().findViewById(by.ssrlab.common_ui.R.id.toolbar_search)
        searchButton.visibility = View.VISIBLE
    }

    private fun disableButtons() {
        val searchButton: ImageButton = requireActivity().findViewById(by.ssrlab.common_ui.R.id.toolbar_search)
        searchButton.visibility = View.GONE

        applyFilter()
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.availableFilters.observe(viewLifecycleOwner) { filters ->
            filters?.let {
                fragmentViewModel.selectedFilters.value?.let { selected ->
                    adapter.updateData(
                        filters,
                        selected.toList()
                    )
                }
            }
        }
    }

    override fun initAdapter() {
        adapter = FilterAdapter(emptyMap(), emptyList()) { filter, isSelected ->
            fragmentViewModel.onFilterSelected(filter, isSelected)
        }

        val filters = fragmentViewModel.availableFilters.value
        val selectedFilters = fragmentViewModel.selectedFilters.value?.toList()
        filters?.let {
            selectedFilters?.let { selected -> adapter.updateData(filters, selected) }
        }

        binding.apply {
            filterDevelopmentsRv.adapter = adapter
            filterDevelopmentsRv.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_invention_filters,
                container,
                false
            )
        return binding.root
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }

    override fun navigateNext(repositoryData: RepositoryData) {
        (activity as MainActivity).moveToExhibit(repositoryData)
    }

    private fun applyFilter() {
        binding.applyFilterButton.setOnClickListener {
            if (fragmentViewModel.selectedFilters.value?.isEmpty() == false) {
                fragmentViewModel.applyFilters()
                fragmentViewModel.setFiltering(true)
                findNavController().navigate(R.id.inventionsFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(by.ssrlab.common_ui.R.string.select_at_least_one_filter),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}