package com.example.zebrabuttonpress.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.zebrabuttonpress.databinding.HomeFragmentBinding
import com.example.zebrabuttonpress.ui.BaseFragment
import com.example.zebrabuttonpress.ui.helper.extension.app
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    override val viewModel: HomeViewModel by viewModels { factory }

    private var _binding: HomeFragmentBinding? = null
    private val binding: HomeFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Home Fragment OnCreate")

    }

    override fun inject() {
        app.appComponent.inject(this)
    }
}