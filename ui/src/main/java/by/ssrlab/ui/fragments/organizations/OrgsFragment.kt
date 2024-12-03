package by.ssrlab.ui.fragments.organizations

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.data.settings.remote.OrganizationLocale
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.domain.utils.Resource
import by.ssrlab.ui.MainActivity
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentOrgsBinding
import by.ssrlab.ui.rv.SectionAdapter
import by.ssrlab.ui.vm.FOrgsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class OrgsFragment : BaseFragment() {

    private lateinit var binding: FragmentOrgsBinding
    private lateinit var adapter: SectionAdapter
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FOrgsVM by activityViewModel<FOrgsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.domain.R.string.folder_organizations))
        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_organizations)
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
            setButtonAction(ButtonAction.SearchAction, ::initSearchBar)
        }

        binding.apply {
            viewModel = this@OrgsFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
        observeOnDataChanged()
        disableButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        clearQuery()
        hideSearchBar()
    }

    override fun onResume() {
        super.onResume()

        if (fragmentViewModel.isFiltering.value == true) {
            // to show results and reset filter button
            showSearchResults()
            binding.resetFilterButton.visibility = View.VISIBLE
            // to prepare for next search
            fragmentViewModel.resetFilters()
        }
    }

    private fun disableButtons() {
        moveToMap()
        moveToFilter()
        initResetButton()
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.orgsData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    adapter.showLoading()
                }

                is Resource.Success -> {
                    adapter.updateData(resource.data)
                    addAvailableFilterCategories()
                    fragmentViewModel.setLoaded(true)
                }

                is Resource.Error -> {
                    adapter.showError(resource.message)
                }
            }
        })
    }

    override fun initAdapter() {
        adapter = SectionAdapter(emptyList()) {
            navigateNext(it)
        }

        when (val resource = fragmentViewModel.orgsData.value) {
            is Resource.Success -> {
                val data = resource.data
                adapter.updateData(data)
            }

            is Resource.Error -> {
                adapter.showError(resource.message)
            }

            is Resource.Loading -> {
                adapter.showLoading()
            }

            null -> {}
        }

        binding.apply {
            orgsRv.adapter = adapter
            orgsRv.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_orgs, container, false)
        return binding.root
    }


    //Map
    private fun moveToMap() {
        binding.orgsMapRipple.setOnClickListener {
            if (fragmentViewModel.isLoaded.value == true) {
                (requireActivity() as MainActivity).moveToMap(fragmentViewModel.getDescriptionArray())
            } else {
                val currentContext = requireContext()
                Toast.makeText(
                    currentContext,
                    currentContext.resources.getString(by.ssrlab.common_ui.R.string.wait_for_data_to_load),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    //Navigation
    override fun onBackPressed() {
        findNavController().navigate(R.id.mainFragment)
        scope.launch {
            // clean the screen without blinking
            delay(500)
            resetFilters()
        }
    }

    override fun navigateNext(repositoryData: RepositoryData) {
        (activity as MainActivity).moveToExhibit(repositoryData)
    }

    //Search
    private var toolbarSearchView: SearchView? = null

    private fun searchBarInstance(): SearchView {
        if (toolbarSearchView == null) {
            toolbarSearchView = requireActivity().findViewById(R.id.toolbar_search_view)
        }
        return toolbarSearchView!!
    }

    override fun filterData(query: String) {
        fragmentViewModel.filterData(query)
    }

    private fun showSearchResults() {
        fragmentViewModel.filteredDataList.value?.let { adapter.updateData(it) }
    }

    private fun initSearchBar() {
        val toolbarSearchView = searchBarInstance()

        toolbarSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterData(it)
                    showSearchResults()
                    return true
                }
                showSearchResults()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterData(it)
                    showSearchResults()
                }
                return true
            }
        })
        toolbarSearchView.visibility = View.VISIBLE
        toolbarSearchView.isIconified = false
        val searchButton: ImageButton = requireActivity().findViewById(R.id.toolbar_search)
        searchButton.visibility = View.GONE

        toolbarSearchView.setOnCloseListener {
            toolbarSearchView.visibility = View.GONE
            searchButton.visibility = View.VISIBLE
            true
        }

        toolbarSearchView.requestFocus()
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(toolbarSearchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
    }

    override fun hideSearchBar() {
        val toolbarSearchView = searchBarInstance()
        toolbarSearchView.visibility = View.GONE
    }

    private fun clearQuery() {
        val toolbarSearchView = searchBarInstance()
        toolbarSearchView.setQuery("", true)
    }


    //Filter
    private fun addAvailableFilterCategories() {
        fragmentViewModel.setAvailableFilters()
    }

    private fun resetFilters() {
        fragmentViewModel.resetFilters()
        fragmentViewModel.setFiltering(false)
        showAllOrgs()
        binding.resetFilterButton.visibility = View.GONE
    }

    private fun showAllOrgs() {
        fragmentViewModel.let {
            if (it.orgsData.value is Resource.Success) {
                val data = (it.orgsData.value as Resource.Success<List<OrganizationLocale>>).data
                adapter.updateData(data)
            }
        }
    }

    private fun initResetButton() {
        binding.resetFilterButton.setOnClickListener { resetFilters() }
    }

    private fun moveToFilter() {
        binding.orgsFilterRipple.setOnClickListener {
            if (fragmentViewModel.isLoaded.value == true) {
                findNavController().navigate(R.id.filterFragment)
            } else {
                val currentContext = requireContext()
                Toast.makeText(
                    currentContext,
                    currentContext.resources.getString(by.ssrlab.common_ui.R.string.wait_for_data_to_load),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}