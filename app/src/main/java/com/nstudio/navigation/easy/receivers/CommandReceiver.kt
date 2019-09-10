package com.nstudio.navigation.easy.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.robotemi.sdk.NlpResult
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnRobotReadyListener
import android.widget.Toast


class CommandReceiver : BroadcastReceiver(), Robot.NlpListener, OnRobotReadyListener{



    lateinit var robot:Robot
    lateinit var context: Context

    private val tag = CommandReceiver::class.java.simpleName

    companion object {
        const val ACTION_HOME_WELCOME = "home.welcome"
        const val ACTION_HOME_DANCE = "home.dance"
        const val ACTION_HOME_SLEEP = "home.sleep"
        const val HOME_BASE_LOCATION = "home base"
    }


    override fun onReceive(context: Context, data: Intent?) {

        this.context = context

        Log.e(tag,"received action")

        robot = Robot.getInstance()
        robot.addNlpListener(this)

    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            try {
//                val componentName = ComponentName(context.packageName, context.packageName+".MainActivity")
//                val activityInfo = context.packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
//                robot.onStart(activityInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }

        }
    }

    override fun onNlpCompleted(nlpResult: NlpResult) {

        Toast.makeText(context, nlpResult.action, Toast.LENGTH_SHORT).show()
        Log.e(tag,"action > ${nlpResult.action} | Query > ${nlpResult.resolvedQuery}")

        when (nlpResult.action) {
            ACTION_HOME_WELCOME -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.mirobotic.radio.singapore.launcher")
                if (intent != null) {
                    context.startActivity(intent)
                }
                robot.tiltAngle(23, 5.3f)
            }

            ACTION_HOME_DANCE -> {
                val t = System.currentTimeMillis()
                val end = t + 5000
                while (System.currentTimeMillis() < end) {
                    robot.skidJoy(0f, 1f)
                }
            }

            ACTION_HOME_SLEEP -> robot.goTo(HOME_BASE_LOCATION)
        }
    }


}