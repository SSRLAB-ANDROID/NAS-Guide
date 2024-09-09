package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.child

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.utils.OrgSubsAdapter
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.DividerItemDecoration
import by.ssrlab.common_ui.common.vm.OrgSubsSharedVM
import by.ssrlab.common_ui.databinding.FragmentAchievementsBinding
import by.ssrlab.data.data.settings.remote.OrganizationLocale
import by.ssrlab.data.util.ButtonAction
import by.ssrlab.domain.models.ToolbarControlObject

class AchievementsFragment : BaseFragment() {

    private lateinit var binding: FragmentAchievementsBinding
    override val fragmentViewModel: OrgSubsSharedVM by viewModels({ requireParentFragment() })

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.orgList.observe(viewLifecycleOwner) { data ->
            initRecyclerView(data ?: emptyList())

            fragmentViewModel.setTitle(requireContext().resources.getString(by.ssrlab.common_ui.R.string.exhibit_achievements))
            activityVM.apply {
                setButtonAction(ButtonAction.BackAction, ::onBackPressed)
            }
        }
    }

    private fun initRecyclerView(achievements: List<OrganizationLocale>) {
        binding.achievementsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = OrgSubsAdapter(achievements) {
                navigateNext(itemDecorationCount)
            }
            addItemDecoration(DividerItemDecoration(context))
        }
    }

    override fun initBinding(container: ViewGroup?): View {

        binding = FragmentAchievementsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onBackPressed() {
        findNavController().popBackStack()
    }
}