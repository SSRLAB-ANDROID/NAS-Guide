package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.child

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.domain.models.ToolbarControlObject

class AchievementsFragment: BaseFragment() {

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: ViewModel
        get() = TODO("Not yet implemented")

    override fun initBinding(container: ViewGroup?): View {
        TODO("Not yet implemented")
    }
}