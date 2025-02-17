package by.ssrlab.ui.fragments.history

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentHistoryBinding
import by.ssrlab.ui.rv.FolderAdapter
import by.ssrlab.ui.vm.FHistoryVM
import org.koin.android.ext.android.get


class HistoryFragment: BaseFragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: FolderAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = true,
        isLang = false,
        isSearch = false,
        isDates = false
    )

    override val fragmentViewModel: FHistoryVM by viewModels {
        FHistoryVM.Factory(get())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.common_ui.R.string.page_history_title))
        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_history)
            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
        }

        binding.apply {
            viewModel = this@HistoryFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
    }

    override fun initAdapter() {
        adapter = FolderAdapter(fragmentViewModel.getData(), { image, resource ->
            loadImage(image, resource)
        }, { address ->
            navigateNext(address)
        })

        binding.apply {
            historyRv.layoutManager = LinearLayoutManager(requireContext())
            historyRv.adapter = adapter
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_history, container, false)
        return binding.root
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }

    override fun navigateNext(address: Int) {
        findNavController().navigate(address)
    }
}