package com.example.zebrabuttonpress.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.zebrabuttonpress.R
import com.example.zebrabuttonpress.ui.helper.extension.app
import kotlinx.android.synthetic.main.main_activity.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MainActivityViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        app.appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        Navigation.findNavController(this, R.id.navHostFragment)
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.layout.splash_fragment -> toolbar.visibility = View.GONE
                    else -> toolbar.visibility = View.VISIBLE
                }
            }

    }
}

