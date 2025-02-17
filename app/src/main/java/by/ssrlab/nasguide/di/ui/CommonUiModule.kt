package by.ssrlab.nasguide.di.ui

import by.ssrlab.common_ui.common.vm.AExhibitVM
import by.ssrlab.common_ui.common.vm.AMainVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val commonUiModule = module {

    viewModel { AMainVM() }
    viewModel { AExhibitVM() }
}