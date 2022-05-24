package com.example.zebrabuttonpress.ui.splash

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.zebrabuttonpress.databinding.SplashFragmentBinding
import com.example.zebrabuttonpress.ui.BaseFragment
import com.example.zebrabuttonpress.ui.MainForegroundService
import com.example.zebrabuttonpress.ui.MainServiceViewModel
import com.example.zebrabuttonpress.ui.helper.extension.app
import com.example.zebrabuttonpress.ui.helper.extension.navigateTo
import timber.log.Timber
import com.example.zebrabuttonpress.R
import javax.inject.Inject

class SplashFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    override val viewModel: SplashViewModel by viewModels { factory }

    @Inject
    lateinit var serviceViewModel: MainServiceViewModel

    private var _binding: SplashFragmentBinding? = null
    private val binding: SplashFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Splash Fragment OnCreate")

        val fallIntent = Intent(this.requireContext(), MainForegroundService::class.java)
        ContextCompat.startForegroundService(this.requireContext(), fallIntent)

        navigateTo(SplashFragmentDirections.actionToHome())
    }

    override fun inject() {
        app.appComponent.inject(this)
    }
}
