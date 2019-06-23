package com.steven.keepscrap

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by steven on 2019/5/10.
 */

class BackgroundCrawler(context: Context) {
    val mContext = context
    var mHandler : Handler = Handler()

    fun crawling() {
        val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT)
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 0
        params.width = 0
        params.height = 0

        val view = LinearLayout(mContext)
        view.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

        val logic = WebViewLogic(mContext, mHandler, WebView(mContext), object : WebViewLogic.Callback{
            override fun onReady(logic: WebViewLogic) {
                mHandler.postDelayed( {
                    val model = PathMonitorViewModel()
                    model.createDb(mContext)
                    model.subscribeToDbChanges()
                    model.getAll().observeForever{ elements ->
                        for (element in elements) {
                            GlobalScope.launch(Dispatchers.Main) {
                                val value = logic.scrap(path = element.path)
                                Recorder.save(mContext, "$value\n")
                            }
                        }
                    }
                }, 1000)
            }

            override fun onElementClick(url: String, path: String, status: Boolean) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }, "https://www.tradingview.com/symbols/BTCUSD/technicals/")
        val wv = logic.webViewIns
        wv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        view.addView(wv)

        windowManager.addView(view, params)
    }

    companion object {
        private val TAG = BackgroundCrawler::class.java.simpleName
    }
}