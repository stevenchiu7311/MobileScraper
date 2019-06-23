package com.steven.keepscrap

import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Created by steven on 2019/5/10.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class BackgroundCrawlerWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    var mHandler : Handler = Handler()
    override fun doWork(): Result {
        mHandler.post {
            BackgroundCrawler(applicationContext).crawling()
        }
        return Result.success()
    }

    companion object {
        private val TAG = BackgroundCrawlerWorker::class.java.simpleName
        private val CHANNEL_ID = "tagfetcher_1"
        private val CHANNEL_NAME = "tagfetcher"
    }
}
