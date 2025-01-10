package by.ssrlab.ui.fragments.inventions

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.MainActivity
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
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val searchButton: ImageButton = requireActivity().findViewById(R.id.toolbar_search)
        searchButton.visibility = View.VISIBLE
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
}