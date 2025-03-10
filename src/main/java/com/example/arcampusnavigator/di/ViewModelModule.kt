package com.example.arcampusnavigator.di

import com.example.arcampusnavigator.ui.auth.AuthViewModel
import com.example.arcampusnavigator.ui.map.MapViewModel
import com.example.arcampusnavigator.ui.ar.ARNavigationViewModel
import com.example.arcampusnavigator.ui.notifications.NotificationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { MapViewModel(get(), get()) }
    viewModel { ARNavigationViewModel(get()) }
    viewModel { NotificationsViewModel(get()) }
}