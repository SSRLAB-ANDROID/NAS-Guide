package by.ssrlab.ui.fragments.history

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
        }

        binding.apply {
            viewModel = this@PersonsFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
        observeOnDataChanged()
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
}