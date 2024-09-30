package by.ssrlab.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.databinding.FragmentSearchBinding
import by.ssrlab.ui.rv.ListAdapter
import by.ssrlab.ui.vm.FCommonSearchVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment() {
    
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: ListAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FCommonSearchVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityVM.apply {
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
        }
    }

    override fun initAdapter() {
        //TODO
    }

    override fun initBinding(container: ViewGroup?): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }
}