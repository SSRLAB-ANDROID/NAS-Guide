package by.ssrlab.ui.fragments.inventions

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseActivity
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.domain.utils.Resource
import by.ssrlab.ui.MainActivity
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentDevelopmentsBinding
import by.ssrlab.ui.rv.SectionAdapter
import by.ssrlab.ui.vm.FDevelopmentsVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class DevelopmentsFragment : BaseFragment() {

    private lateinit var binding: FragmentDevelopmentsBinding
    private lateinit var adapter: SectionAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FDevelopmentsVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.domain.R.string.folder_inventions))
        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_inventions)
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
            setButtonAction(ButtonAction.SearchAction, ::initSearchBar)
        }

        binding.apply {
            viewModel = this@DevelopmentsFragment.fragmentViewModel
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

    private fun disableButtons() {
        moveToFilter()
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.inventionsData.observe(viewLifecycleOwner, Observer { resource ->
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
        })
    }

    override fun initAdapter() {
        adapter = SectionAdapter(emptyList()) {
            navigateNext(it)
        }

        when (val resource = fragmentViewModel.inventionsData.value) {
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
            inventionsRv.adapter = adapter
            inventionsRv.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_developments,
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
    private fun moveToFilter() {
        binding.inventionsFilterRipple.setOnClickListener {
            if (fragmentViewModel.isLoaded.value == true) {
                (requireActivity() as BaseActivity).createIsntRealizedDialog()
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