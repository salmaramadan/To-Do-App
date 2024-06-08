package com.salma.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_ToDo_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_ToDo_Light)
            Configuration.UI_MODE_NIGHT_UNDEFINED -> setTheme(R.style.Theme_ToDo_Light)
        }

        setContentView(R.layout.activity_splash)

        createNotificationChannel()

        Handler(Looper.getMainLooper()).postDelayed({
            val preferences: SharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
            val isFirstLaunch = preferences.getBoolean(Constants.FIRST_LAUNCH_KEY, true)
            if (isFirstLaunch) {
                val guideIntent = Intent(this, GuideActivity::class.java)
                startActivity(guideIntent)
                preferences.edit().putBoolean(Constants.FIRST_LAUNCH_KEY, false).apply()
            } else {
                val noteIntent = Intent(this, NoteActivity::class.java)
                startActivity(noteIntent)
            }
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "1"
            val channelName = "Your Channel Name"
            val channelDescription = "Your Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

object Constants {
    const val PREF_NAME = "TO Do"
    const val FIRST_LAUNCH_KEY = "first launch key"
}
