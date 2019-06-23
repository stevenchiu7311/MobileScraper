package com.steven.keepscrap

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * Created by steven on 2019/5/10.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class BackgroundCrawlerService : JobService() {

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(applicationContext, CHANNEL_ID).build()
        //startForeground(1, notification);

        BackgroundCrawler(this).crawling()
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.i(TAG, "onStartJob jobParameters:$jobParameters")
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.i(TAG, "onStopJob jobParameters:$jobParameters")
        return false
    }

    companion object {
        private val TAG = BackgroundCrawlerService::class.java.simpleName
        private val CHANNEL_ID = "tagfetcher_1"
        private val CHANNEL_NAME = "tagfetcher"
    }
}
