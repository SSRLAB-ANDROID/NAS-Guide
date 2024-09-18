package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.child

import android.view.View
import android.view.ViewGroup
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.common_ui.common.vm.AExhibitVM
import by.ssrlab.common_ui.databinding.FragmentContactsBinding
import by.ssrlab.domain.models.ToolbarControlObject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ContactsFragment : BaseFragment() {

    private lateinit var binding: FragmentContactsBinding

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: AExhibitVM by activityViewModel()

    override fun initBinding(container: ViewGroup?): View {
        binding = FragmentContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}