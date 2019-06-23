package com.steven.keepscrap

import android.content.Context
import android.os.Handler
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Created by steven on 2019/5/10.
 */
class ScraperWebViewClient(jsStr: String, stayingUrl: String? = null, cb: Callback? = null) : WebViewClient() {
    private val mJsStr: String = jsStr
    private val mCb: Callback? = cb
    private var mUrl: String? = null
    var stayingUrl: String? = stayingUrl
        set(value) {mUrl = value}

    private var mHyperLinkLock: Boolean = false
    var hyperLinkLock: Boolean
        set(value) {mHyperLinkLock = value}
        get() {return mHyperLinkLock}

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return if (mUrl == null) {
            view.loadUrl(url)
            false
        } else {
            if (!mHyperLinkLock) {
                view.loadUrl(url)
            }
            true
        }
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        Log.i(MainFragment.TAG, "mJsStr:$mJsStr")
        view.loadUrl("javascript:$mJsStr")
        mCb?.onPageFinished()
    }

    interface Callback {
        fun onPageFinished()
    }
}

class WebViewLogic(context: Context, handler: Handler, wv: WebView, cb: Callback, targetUrl: String){
    private val mWebView: WebView = wv
    private val mCb: Callback? = cb
    private var mTargetUrl: String = targetUrl
    private val mHandler: Handler = handler
    private val mWebViewClient: ScraperWebViewClient

    init {
        val js = context.assets.open("scraper.js").bufferedReader().use { it.readText() }
        wv.settings.useWideViewPort = true
        wv.settings.javaScriptEnabled = true
        wv.addJavascriptInterface(JavaScriptInterface(context, object:JavaScriptInterface.Callback {
            override fun onElementClick(path: String) {
                GlobalScope.launch(Dispatchers.Main) {
                    val status: Boolean = withContext(Dispatchers.Main) {
                        toggleSelection(path = path)
                    }
                    mCb?.onElementClick(mTargetUrl, path, status)
                }
            }
        }), "android")
        mWebViewClient = ScraperWebViewClient(js, stayingUrl = targetUrl, cb = object: ScraperWebViewClient.Callback{
            override fun onPageFinished() {
                mCb?.onReady(this@WebViewLogic)
            }
        })
        wv.webViewClient = mWebViewClient
        wv.settings.userAgentString = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
        wv.loadUrl(mTargetUrl)
    }

    fun targetUrl(): String {
        return mTargetUrl
    }

    fun loadUrl(url: String) {
        mTargetUrl = url
        mWebViewClient.stayingUrl = url
        mWebView.loadUrl(url)
    }

    fun toggleHyperLink() {
        mWebViewClient.hyperLinkLock = !mWebViewClient.hyperLinkLock
    }

    suspend fun scrap(path: String) : String? {
        return suspendCoroutine { cont ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mHandler.post {
                    mWebView.evaluateJavascript("javascript: highlightTarget('$path');", null)
                    mWebView.evaluateJavascript("javascript:getText('$path');", object: ValueCallback<String> {
                        override fun onReceiveValue(value: String?) {
                            Log.d(TAG, "JS: $value")
                            cont.resume(value)
                        }
                    })
                }
            } else {
                cont.resumeWithException(Throwable("WebView evaluateJavascript is not supported..."))
            }
        }
    }

    suspend fun highlight(path: String) {
        return suspendCoroutine { cont ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mHandler.post {
                    mWebView.evaluateJavascript("javascript: highlightTarget('$path');", null)
                }
            } else {
                cont.resumeWithException(Throwable("WebView evaluateJavascript is not supported..."))
            }
        }
    }

    suspend fun toggleSelection(status: Boolean? = null, path: String): Boolean {
        return suspendCoroutine { cont ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                val statusParam: String = status?.toString() ?: "undefined"
                mHandler.post {
                    mWebView.evaluateJavascript("javascript: toggleSelection($statusParam, '$path');", object: ValueCallback<String> {
                        override fun onReceiveValue(value: String) {
                            cont.resume(value == "true")
                        }
                    })
                }
            } else {
                cont.resumeWithException(Throwable("WebView evaluateJavascript is not supported..."))
            }
        }
    }

    val webViewIns: WebView
        get() = mWebView

    interface Callback {
        fun onElementClick(url: String, path: String, status: Boolean)
        fun onReady(logic: WebViewLogic)
    }

    companion object {
        val TAG = WebViewLogic::class.java.simpleName
    }
}