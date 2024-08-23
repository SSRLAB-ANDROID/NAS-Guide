package by.ssrlab.ui.fragments.history

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.domain.utils.Resource
import by.ssrlab.ui.MainActivity
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentPlacesBinding
import by.ssrlab.ui.rv.SectionAdapter
import by.ssrlab.ui.vm.FPlacesVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlacesFragment: BaseFragment() {

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
        }

        binding.apply {
            viewModel = this@PlacesFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner

            placesMapRipple.setOnClickListener {
                (requireActivity() as MainActivity).moveToMap(fragmentViewModel.getDescriptionArray())
            }
        }

        initAdapter()
        observeOnDataChanged()
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.placesData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    adapter.showLoading()
                }
                is Resource.Success -> {
                    adapter.updateData(resource.data)
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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_places, container, false)
        return binding.root
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }

    override fun navigateNext(repositoryData: RepositoryData) {
        (activity as MainActivity).moveToExhibit(repositoryData)
    }
}