package com.example.bibliohub

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import com.example.bibliohub.data.ProductList
import com.example.bibliohub.fragments.login.LoginViewModel
import com.example.bibliohub.utils.Constants
import com.example.bibliohub.utils.Constants.IS_LOGGED_IN
import com.example.bibliohub.utils.HelperFunctions.createMediaDirectory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    private val PERMISSION_REQUEST_CODE = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.nav_graph)

        createDirs()

        val isLoggedIn = viewModel.biblioHubPreferencesRepository.getPreference(
            Boolean::class.java,
            IS_LOGGED_IN
        )
        val isAdmin = viewModel.biblioHubPreferencesRepository.getPreference(
            Boolean::class.java,
            Constants.IS_ADMIN
        )
        isLoggedIn.observe(this@MainActivity) {
            if (it == true) {
                isAdmin.observe(this@MainActivity) { admin ->
                    Log.d("AppActivity", admin.toString())
                    if (admin == true) {
                        val intent = Intent(this@MainActivity, AdminActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@MainActivity, CustomerActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                navGraph.setStartDestination(startDestId = R.id.loginFragment)
            }
            val navController = navHostFragment.navController
            navController.setGraph(navGraph, intent.extras)
        }

        if (!checkPermission())
            requestPermission()

        viewModel.insertProducts()
    }

    /**
     * Create image directory if it does not exist
     */
    private fun createDirs() {
        try {
            createMediaDirectory(
                this,
                Constants.PRODUCT_PICTURE_DIR,
                Environment.DIRECTORY_PICTURES
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.INTERNET),
            PERMISSION_REQUEST_CODE
        )
    }
}