package by.ssrlab.nasguide.di.ui

import by.ssrlab.domain.models.SharedPreferencesUtil
import org.koin.dsl.module

val sharedPreferencesUtilModule = module {

    single { SharedPreferencesUtil(get()) }
}