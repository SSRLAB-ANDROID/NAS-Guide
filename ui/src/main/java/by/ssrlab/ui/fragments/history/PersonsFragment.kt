package by.ssrlab.ui.fragments.history

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.MainActivity
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentPersonsBinding
import by.ssrlab.ui.rv.GridAdapter
import by.ssrlab.ui.vm.FPersonsVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class PersonsFragment : BaseFragment() {

    private lateinit var binding: FragmentPersonsBinding
    private lateinit var adapter: GridAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FPersonsVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.common_ui.R.string.page_persons_title))
        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_persons)
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
            setButtonAction(ButtonAction.SearchAction, ::initSearchBar)
        }

        binding.apply {
            viewModel = this@PersonsFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
        observeOnDataChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        clearQuery()
        hideSearchBar()
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.personsData.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    override fun initAdapter() {
        adapter = GridAdapter(fragmentViewModel.personState.value.personList?.toList()!!) {
            navigateNext(it)
        }

        binding.apply {
            personsRv.adapter = adapter
            personsRv.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_persons, container, false)
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

        toolbarSearchView.requestFocus()
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(toolbarSearchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
    }

    override fun hideSearchBar() {
        val toolbarSearchView = searchBarInstance()
        toolbarSearchView.visibility = View.GONE
    }

    private fun clearQuery (){
        val toolbarSearchView = searchBarInstance()
        toolbarSearchView.setQuery(" ", true)
    }
}