package com.nstudio.navigation.easy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.nstudio.navigation.easy.common.AppSettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    }

    private lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        appSettings = AppSettings(this@MainActivity)

        btnOpen.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setTitle("Permission Needed")
                alertDialog.setMessage("This app needs draw over screen permission. please allow this to use app.")
                alertDialog.setPositiveButton("Allow"){ _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
                }

                alertDialog.setCancelable(false)
                alertDialog.show()


            } else {
                startApp()
            }
        }

        btnStop.setOnClickListener { FloatingViewService.getsSharedInstance()?.stopSelf() }

        init()
    }

    override fun onResume() {
        super.onResume()

        val accessibility = isAccessibilitySettingsOn()
        //Fix Android O bug
        if (!accessibility) {
            val alertDialog = AlertDialog.Builder(this@MainActivity)
            alertDialog.setTitle("Permission Needed")
            alertDialog.setMessage("This app needs Accessibility permission. please allow this to use app.")
            alertDialog.setPositiveButton("Allow"){ _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            alertDialog.setNegativeButton("Latter"){_,_ -> }
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    private fun isAccessibilitySettingsOn(): Boolean {
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                this.contentResolver,
                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            //accessibility is Enable
            if (BuildConfig.DEBUG) {
                Log.i("MainActivity", "Error >> "+e.message)
            }
        }

        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                this.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (services != null) {
                return services.toLowerCase().contains(this.packageName.toLowerCase())
            }
        }
        return false
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
//
//            if (resultCode == Activity.RESULT_OK) {
//                startApp()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Draw over other app permission not available. Closing the application",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                finish()
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//
//    }

    private fun startApp() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)){
                startService(Intent(this@MainActivity, FloatingViewService::class.java))
                finishAndRemoveTask()
                return
            }
            Toast.makeText(this@MainActivity,"Permission Denied",Toast.LENGTH_SHORT).show()
        } else {
            startService(Intent(this@MainActivity, FloatingViewService::class.java))
            finishAndRemoveTask()
        }

    }

    private fun init(){

        swMic.isChecked = appSettings.isShowMic()
        swAppDrawer.isChecked = appSettings.isAppDrawer()
        swCloseOpenApp.isChecked = appSettings.isCloseApp()

        if(appSettings.getOrientation()==AppSettings.VERTICAL){
            rbVertical.isChecked = true
        }else{
            rbHorizontal.isChecked = true
        }

        swMic.setOnCheckedChangeListener { _, show ->
            run {
                appSettings.setShowMic(show)
                updateView();
            }
        }
        swAppDrawer.setOnCheckedChangeListener { _, show ->
            run {
                appSettings.setAppDrawer(show)
                updateView()
            }
        }
        swCloseOpenApp.setOnCheckedChangeListener { _, show ->
            run {
                appSettings.setCloseApp(show)
                updateView()
            }
        }

        rbParent.setOnCheckedChangeListener { _, i ->
            run {
                if (i == R.id.rbHorizontal) {
                    appSettings.setOreientation(AppSettings.HORIZONTAL)
                } else {
                    appSettings.setOreientation(AppSettings.VERTICAL)
                }
                updateView()
            }
        }


    }

    private fun updateView() {
       val floatingViewService = FloatingViewService.getsSharedInstance()
        floatingViewService?.updateView()
    }

}
