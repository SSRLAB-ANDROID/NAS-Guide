package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.child

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import by.ssrlab.common_ui.common.ui.base.BaseFragment
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.DividerItemDecoration
import by.ssrlab.common_ui.common.vm.AExhibitVM
import by.ssrlab.common_ui.databinding.FragmentDepartmentsBinding
import by.ssrlab.data.data.remote.DepartmentFilter
import by.ssrlab.data.data.remote.Development
import by.ssrlab.domain.models.ToolbarControlObject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DepartmentsFragment: BaseFragment() {

    private lateinit var binding: FragmentDepartmentsBinding

    override val toolbarControlObject = ToolbarControlObject(
        isBack = false,
        isLang = true,
        isSearch = true,
        isDates = false
    )

    override val fragmentViewModel: AExhibitVM by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val developmentData = arguments?.getParcelable<Development>("development_data")
        val department = developmentData?.departmentFilter!!

        initRecyclerView(department)
    }

    private fun initRecyclerView(department: DepartmentFilter) {
        binding.departmentsRv.apply {
            layoutManager = LinearLayoutManager(context)
//            adapter = DepartmentAdapter(department)
            addItemDecoration(DividerItemDecoration(context))
        }
    }

    override fun initBinding(container: ViewGroup?): View {
        binding = FragmentDepartmentsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}