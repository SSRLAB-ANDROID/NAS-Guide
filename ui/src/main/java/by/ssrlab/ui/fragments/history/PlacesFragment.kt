package by.ssrlab.ui.fragments.history

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.domain.utils.Resource
import by.ssrlab.common_ui.common.ui.MainActivity
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentPlacesBinding
import by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.NavigationManager
import by.ssrlab.ui.rv.SectionAdapter
import by.ssrlab.ui.vm.FPlacesVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlacesFragment : BaseFragment() {

    private lateinit var binding: FragmentPlacesBinding
    private lateinit var adapter: SectionAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FPlacesVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.common_ui.R.string.page_places_title))
        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_places)
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
            setButtonAction(ButtonAction.SearchAction, ::initSearchBar)
        }

        binding.apply {
            viewModel = this@PlacesFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
        disableButtons()
        observeOnDataChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        clearQuery()
        hideSearchBar()
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.placesData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    adapter.showLoading()
                }

                is Resource.Success -> {
                    adapter.updateData(resource.data)
                    fragmentViewModel.setLoaded(true)
                }

                is Resource.Error -> {
                    adapter.showError(resource.message)
                }
            }
        }
    }

    override fun initAdapter() {
        adapter = SectionAdapter(
            emptyList(), NavigationManager
        ) { NavigationManager.handleNavigate(activity as MainActivity) }

        when (val resource = fragmentViewModel.placesData.value) {
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

            null -> TODO()
        }

        binding.apply {
            placesRv.adapter = adapter
            placesRv.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_places, container, false)
        return binding.root
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }

    override fun navigateNext(repositoryData: RepositoryData) {
        (activity as MainActivity).moveToExhibit(repositoryData)
    }

    private fun disableButtons() {
        moveToMap()
    }

    //Search
    private var toolbarSearchView: SearchView? = null

    private fun searchBarInstance(): SearchView {
        if (toolbarSearchView == null) {
            toolbarSearchView = requireActivity().findViewById(by.ssrlab.common_ui.R.id.toolbar_search_view)
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
        val searchButton: ImageButton = requireActivity().findViewById(by.ssrlab.common_ui.R.id.toolbar_search)
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


    //Map
    private fun moveToMap() {
        binding.placesMapRipple.setOnClickListener {
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
}