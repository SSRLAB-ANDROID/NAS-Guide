package by.ssrlab.nasguide.di.ui

import by.ssrlab.domain.repository.ui.UiDataProvider
import org.koin.dsl.module

val uiProviderModule = module {

    single { UiDataProvider() }
}