package by.ssrlab.ui.fragments.history

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.fragments.BaseFragment
import by.ssrlab.domain.models.ToolbarControlObject
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

        fragmentViewModel.setTitle(requireContext().resources.getString(R.string.page_places_title))
        activityVM.setHeaderImg(by.ssrlab.common_ui.R.drawable.header_places)

        binding.apply {
            viewModel = this@PlacesFragment.fragmentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        initAdapter()
        observeOnDataChanged()
    }

    override fun observeOnDataChanged() {
        fragmentViewModel.placesData.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    override fun initAdapter() {
        adapter = SectionAdapter(fragmentViewModel.placesData.value!!) {
            navigateNext()
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

    override fun navigateNext() {
        (activity as MainActivity).moveToExhibit()
    }
}