package com.example.zebrabuttonpress.ui.splash

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.zebrabuttonpress.R
import com.example.zebrabuttonpress.ui.BaseFragment
import com.example.zebrabuttonpress.ui.MainForegroundService
import com.example.zebrabuttonpress.ui.MainServiceViewModel
import com.example.zebrabuttonpress.ui.helper.ViewState
import com.example.zebrabuttonpress.ui.helper.extension.app
import com.example.zebrabuttonpress.ui.helper.extension.navigateTo
import javax.inject.Inject

class SplashFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    override val viewModel: SplashViewModel by viewModels { factory }

    @Inject
    lateinit var serviceViewModel: MainServiceViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fallIntent = Intent(this.requireContext(), MainForegroundService::class.java)
        ContextCompat.startForegroundService(this.requireContext(), fallIntent)
        navigateTo(SplashFragmentDirections.actionToHome())
    }

    override fun inject() {
        app.appComponent.inject(this)
    }
}
