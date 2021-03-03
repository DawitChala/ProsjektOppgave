package com.example.IN2000_prosjekt

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.IN2000_prosjekt.Info.InfoViewModel
import com.example.IN2000_prosjekt.Settings.SettingsViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val infoViewModel: InfoViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel = SettingsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.viewHenter(this)
        infoViewModel.viewHenter(this)
        settingsViewModel.passMain(this)



        //Fikser nav:
        setupViews()


    }


    private fun setupViews() {
        // Finner Navigation Controller
        val navController = findNavController(R.id.fragNavHost)

        // Setter Navigation Controller til BottomNavigationView
        bottomNavView.setupWithNavController(navController)


        // Setter opp ActionBar med Navigation Controller
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }

     override fun onRequestPermissionsResult(
     requestCode: Int,
     permissions: Array<String>,
     grantResults: IntArray
     ) {
     if (requestCode == viewModel.getPermissionId()) {
         if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
             viewModel.getLastLocation()
         }
     }
 }


}

