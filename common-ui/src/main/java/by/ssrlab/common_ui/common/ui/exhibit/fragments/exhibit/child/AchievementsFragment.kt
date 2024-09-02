package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.child

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.DividerItemDecoration
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.OrgsAdditionalsAdapter
import by.ssrlab.common_ui.common.vm.OrgsAdditionalsSharedVM
import by.ssrlab.common_ui.databinding.FragmentAchievementsBinding
import by.ssrlab.data.data.settings.remote.OrganizationLocale
import by.ssrlab.domain.models.ToolbarControlObject

class AchievementsFragment : BaseFragment() {

    private lateinit var binding: FragmentAchievementsBinding

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )
    override val fragmentViewModel: OrgsAdditionalsSharedVM by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val achievements = fragmentViewModel.orgsList.value
        initRecyclerView(achievements ?: emptyList())
    }

    private fun initRecyclerView(achievements: List<OrganizationLocale>) {

        binding.achievementsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = OrgsAdditionalsAdapter(achievements) {
                navigateNext(itemDecorationCount)
            }
            addItemDecoration(DividerItemDecoration(context))
        }
    }

    override fun initBinding(container: ViewGroup?): View {

        binding = FragmentAchievementsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}