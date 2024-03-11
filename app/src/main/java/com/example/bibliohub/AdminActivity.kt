package com.example.bibliohub

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.bibliohub.fragments.login.LoginViewModel
import com.example.bibliohub.utils.HelperFunctions

class AdminActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    private var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.admin_nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment?.navController?.navInflater
        val navGraph = inflater?.inflate(R.navigation.admin_nav_graph)
        navGraph?.setStartDestination(startDestId = R.id.adminHomeFragment)

        HelperFunctions.setAdminMenu(
            this,
            viewModel.biblioHubPreferencesRepository
        )
    }
}