package by.ssrlab.ui.fragments.inventions

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
import by.ssrlab.ui.databinding.FragmentDevelopmentsBinding
import by.ssrlab.ui.rv.SectionAdapter
import by.ssrlab.ui.vm.FDevelopmentsVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class DevelopmentsFragment: BaseFragment() {

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
        }

        binding.apply {
            viewModel = this@DevelopmentsFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
        observeOnDataChanged()
        disableButtons()
    }

    private fun disableButtons() {
        binding.inventionsFilterRipple.setOnClickListener {
            (requireActivity() as BaseActivity).createIsntRealizedDialog()
        }
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.inventionsData.observe(viewLifecycleOwner, Observer { resource ->
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

//    override fun navigateNext(repositoryData: RepositoryData) {
//        if (repositoryData is DevelopmentLocale) {
//            val bundle = Bundle().apply {
//                putParcelable("development_data", repositoryData)
//            }
//            findNavController().navigate(by.ssrlab.common_ui.R.id.action_labsFragment_to_departmentsFragment, bundle)
//        } else{
//            (activity as MainActivity).moveToExhibit(repositoryData)
//        }
//    }


//va.lang.IllegalArgumentException: Navigation action/destination by.ssrlab.nasguide:id/
// action_labsFragment_to_departmentsFragment cannot be found from the current destination 
// Destination(by.ssrlab.nasguide:id/inventionsFragment) label=InventionsFragment 
// class=by.ssrlab.ui.fragments.inventions.DevelopmentsFragment
//  a.lang.IllegalArgumentException: Navigation action/destination
//  by.ssrlab.nasguide:id/action_exhibitFragment_to_achievementsFragment cannot be found
//  from the current destination Destination(by.ssrlab.nasguide:id/orgFragment)
//  label=OrgFragment class=by.ssrlab.ui.fragments.organizations.OrgsFragment
//

}