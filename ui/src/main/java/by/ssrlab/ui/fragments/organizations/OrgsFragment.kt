package by.ssrlab.ui.fragments.organizations

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import by.ssrlab.ui.databinding.FragmentOrgsBinding
import by.ssrlab.ui.rv.SectionAdapter
import by.ssrlab.ui.vm.FOrgsVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrgsFragment: BaseFragment() {

    private lateinit var binding: FragmentOrgsBinding
    private lateinit var adapter: SectionAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FOrgsVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.domain.R.string.folder_organizations))
        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_organizations)
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
        }

        binding.apply {
            viewModel = this@OrgsFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner

            orgsMapRipple.setOnClickListener {
                (requireActivity() as MainActivity).moveToMap(fragmentViewModel.getDescriptionArray())
            }
        }

        initAdapter()
        observeOnDataChanged()
        disableButtons()
    }

    private fun disableButtons() {
        binding.orgsFilterRipple.setOnClickListener {
            (requireActivity() as BaseActivity).createIsntRealizedDialog()
        }
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.orgsData.observe(viewLifecycleOwner, Observer { resource ->
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

    override fun onBackPressed() {
        findNavController().popBackStack()
    }

    override fun navigateNext(repositoryData: RepositoryData) {
        (activity as MainActivity).moveToExhibit(repositoryData)
    }
}