package com.nstudio.navigation.easy.common

import android.content.Context
import android.content.SharedPreferences

class AppSettings(val context: Context){

    companion object {
        const val VERTICAL = 1
        const val HORIZONTAL = 0
    }

    private val sharedPreferences:SharedPreferences = context.getSharedPreferences("settings",Context.MODE_PRIVATE)

    fun isShowMic() : Boolean{
        return sharedPreferences.getBoolean("mic",false)
    }

    fun isCloseApp() : Boolean{
        return sharedPreferences.getBoolean("closeRunningApp",false)
    }

    fun isAppDrawer() : Boolean{
        return sharedPreferences.getBoolean("appDrawer",false)
    }

    fun getOrientation() : Int{
        return sharedPreferences.getInt("orientation", VERTICAL)
    }

    fun setOreientation(orientation:Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("orientation",orientation)
        editor.apply()
    }


    fun setShowMic(show:Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean("mic",show)
        editor.apply()
    }

    fun setCloseApp(show:Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean("closeRunningApp",show)
        editor.apply()
    }

    fun setAppDrawer(show:Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean("appDrawer",show)
        editor.apply()
    }


}