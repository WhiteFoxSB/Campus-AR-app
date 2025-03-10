package com.example.arcampusnavigator.di

import com.example.arcampusnavigator.data.repository.AuthRepository
import com.example.arcampusnavigator.data.repository.AuthRepositoryImpl
import com.example.arcampusnavigator.data.repository.CampusRepository
import com.example.arcampusnavigator.data.repository.CampusRepositoryImpl
import com.example.arcampusnavigator.data.repository.NotificationRepository
import com.example.arcampusnavigator.data.repository.NotificationRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<CampusRepository> { CampusRepositoryImpl(get(), get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get(), get()) }
}