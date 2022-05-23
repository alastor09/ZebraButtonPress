package com.example.zebrabuttonpress.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zebrabuttonpress.ui.MainActivityViewModel
import com.example.zebrabuttonpress.ui.home.HomeViewModel
import com.example.zebrabuttonpress.ui.splash.SplashViewModel
import com.example.zebrabuttonpress.injection.viewmodel.ViewModelFactory
import com.example.zebrabuttonpress.injection.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel
}