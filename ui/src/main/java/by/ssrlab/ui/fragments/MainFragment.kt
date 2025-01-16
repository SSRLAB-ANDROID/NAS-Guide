package by.ssrlab.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.common_ui.common.util.createSimpleAlertDialog
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject
import by.ssrlab.common_ui.common.ui.MainActivity
import by.ssrlab.ui.R
import by.ssrlab.ui.databinding.FragmentMainBinding
import by.ssrlab.ui.rv.FolderAdapter
import by.ssrlab.ui.vm.FMainVM
import org.koin.android.ext.android.get

class MainFragment: BaseFragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FolderAdapter

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: FMainVM by viewModels {
        FMainVM.Factory(get())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.common_ui.R.string.page_main_title))
        activityVM.apply {
            setHeaderImg(by.ssrlab.common_ui.R.drawable.header_main)

            setButtonAction(ButtonAction.BackAction, ::onBackPressed)
            setButtonAction(ButtonAction.LanguageAction, ::initLanguageDialog)

            //reinit when functionality is available (and clear the func)
            setButtonAction(ButtonAction.SearchAction, ::createIsntRealizedDialog)
        }

        binding.apply {
            viewModel = this@MainFragment.fragmentViewModel
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
            mainRv.layoutManager = LinearLayoutManager(requireContext())
            mainRv.adapter = adapter
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onBackPressed() {
        (requireActivity() as MainActivity).finish()
    }

    override fun navigateNext(address: Int) {
        findNavController().navigate(address)
    }

    private fun createIsntRealizedDialog() {
        createSimpleAlertDialog(
            this.getString(by.ssrlab.common_ui.R.string.dialog_dont_available),
            this.getString(by.ssrlab.common_ui.R.string.dialog_isnt_realized),
            this.getString(by.ssrlab.common_ui.R.string.dialog_ok),
            requireActivity()
        )
    }
}