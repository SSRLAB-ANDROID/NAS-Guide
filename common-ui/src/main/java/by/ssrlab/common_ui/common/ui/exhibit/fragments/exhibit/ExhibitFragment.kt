package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.ssrlab.common_ui.common.ui.base.BaseActivity
import by.ssrlab.common_ui.common.ui.exhibit.ExhibitActivity
import by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.utils.OrgSubs
import by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit.utils.OrgSubsAdapter
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.FragmentSettingsManager
import by.ssrlab.common_ui.common.ui.exhibit.fragments.utils.MediaPlayer.pauseAudio
import by.ssrlab.common_ui.common.vm.AExhibitVM
import by.ssrlab.common_ui.common.vm.OrgSubsSharedVM
import by.ssrlab.common_ui.databinding.FragmentExhibitBinding
import by.ssrlab.data.data.common.RepositoryData
import by.ssrlab.data.data.settings.remote.DevelopmentLocale
import by.ssrlab.data.data.settings.remote.OrganizationLocale
import by.ssrlab.data.data.settings.remote.PersonLocale
import by.ssrlab.data.data.settings.remote.PlaceLocale
import by.ssrlab.data.util.ExhibitObject
import by.ssrlab.domain.utils.fromHtml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ExhibitFragment : Fragment() {

    private lateinit var binding: FragmentExhibitBinding
    private val activityViewModel: AExhibitVM by activityViewModel()
    private val orgSubsViewModel: OrgSubsSharedVM by viewModels()
    private lateinit var exhibitActivity: ExhibitActivity
    private lateinit var data: RepositoryData
    private lateinit var fragmentSettingsManager: FragmentSettingsManager
    private lateinit var orgSubsAdapter: OrgSubsAdapter
    private var orgList: List<OrganizationLocale> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exhibitActivity = requireActivity() as ExhibitActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExhibitBinding.inflate(layoutInflater, container, false)

        fragmentSettingsManager = FragmentSettingsManager(
            binding = binding,
            exhibitActivity = exhibitActivity
        )

        lifecycleScope.launch {
            val audio = withContext(Dispatchers.IO) {
                activityViewModel.repositoryData.value?.audio
            }

            if (audio != null) {
                fragmentSettingsManager.initMediaPlayerWithString(audio)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOnParcelableData()
        disableButtons()

        orgSubsViewModel.orgList.observe(viewLifecycleOwner) { data ->
            orgList = data
        }
    }

    override fun onStop() {
        super.onStop()

        pauseAudio()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        fragmentSettingsManager.destroyPlayer()
    }

    private fun disableButtons() {
        binding.apply {
            exhibitPreviousRipple.setOnClickListener { (requireActivity() as BaseActivity).createIsntRealizedDialog() }
            exhibitNextRipple.setOnClickListener { (requireActivity() as BaseActivity).createIsntRealizedDialog() }
            exhibitContactsRipple.setOnClickListener { (requireActivity() as BaseActivity).createIsntRealizedDialog() }
            exhibitLabsRipple.setOnClickListener { (requireActivity() as BaseActivity).createIsntRealizedDialog() }
            exhibitAchievementsRipple.setOnClickListener {
                if (orgList.isNotEmpty()) {
                setDataType(OrgSubs.Achievements)
                navigateOrgSubs()
                } else {
                    Toast.makeText(requireContext(), "No organizations found", Toast.LENGTH_SHORT).show()
                }
            }
            exhibitResearchRipple.setOnClickListener { (requireActivity() as BaseActivity).createIsntRealizedDialog() }
            exhibitDeveloperRipple.setOnClickListener { (requireActivity() as BaseActivity).createIsntRealizedDialog() }
        }
    }

    private fun observeOnParcelableData() {
        activityViewModel.repositoryData.observe(viewLifecycleOwner) {
            if (it != null) {
                data = it
                activityViewModel.setHeader(data.description!!.image.address)
            }

            when (it) {
                is DevelopmentLocale -> setParametersVisibility(ExhibitObject.Development)
                is OrganizationLocale -> setParametersVisibility(ExhibitObject.Organization)
                is PersonLocale -> setParametersVisibility(ExhibitObject.Person)
                is PlaceLocale -> setParametersVisibility(ExhibitObject.Place)
                null -> TODO()
            }
        }
    }

    private fun setParametersVisibility(exhibitObject: ExhibitObject) {
        binding.apply {
            when (exhibitObject) {
                ExhibitObject.Development -> {
                    exhibitDeveloperBlock.visibility = View.VISIBLE
                }

                ExhibitObject.Organization -> {
                    exhibitAdditionalBlock.visibility = View.VISIBLE
                    exhibitButtonMap.visibility = View.VISIBLE
                    exhibitMapRipple.setOnClickListener {
                        exhibitActivity.moveToMapFromExhibit(data)
                    }

                }

                ExhibitObject.Person -> {}
                ExhibitObject.Place -> {
                    exhibitButtonMap.visibility = View.VISIBLE
                    exhibitMapRipple.setOnClickListener {
                        exhibitActivity.moveToMapFromExhibit(data)
                    }
                }
            }

            if (data.audio != null) {
                exhibitPlayerBlock.visibility = View.VISIBLE
            }

            setParametersValues(exhibitObject)
        }
    }

    private fun setParametersValues(exhibitObject: ExhibitObject) {
        val data = activityViewModel.repositoryData.value!!

        binding.apply {
            exhibitTitle.text = data.name
            exhibitBody.text = data.about.fromHtml()

            if (exhibitObject is ExhibitObject.Development)
                exhibitDeveloperTitle.text =
                    (data as DevelopmentLocale).description.departmentFilter.keyName
            if (exhibitObject is ExhibitObject.Organization) {
                initOrgSubsAdapter()
            }
        }
    }

    private fun initOrgSubsAdapter() {
        if (orgSubsViewModel.orgList.isInitialized) {
            orgSubsAdapter =
                OrgSubsAdapter(orgSubsViewModel.orgList.value!!) {
                    navigateOrgSubs()
                }
            orgSubsAdapter.dataType = OrgSubs.Contacts
        }
    }

    private fun setDataType(dataType: OrgSubs) {
        orgSubsAdapter.dataType = dataType
    }

    private fun navigateOrgSubs() {
        orgSubsAdapter.navigateOrgSubs()
    }
}