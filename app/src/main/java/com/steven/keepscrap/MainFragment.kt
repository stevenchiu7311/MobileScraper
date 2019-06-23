package com.steven.keepscrap

import android.app.AlarmManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.steven.keepscrap.db.WatchingElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


class MainFragment : Fragment() {
    private val mHandler: Handler = Handler()
    private lateinit var mModel: PathMonitorViewModel
    private lateinit var mLogic: WebViewLogic
    private val mMap = HashMap<String, ArrayList<String>>()
    private var mOpenedUrl: String = "https://www.google.com"

    companion object {
        val TAG: String = this.javaClass.simpleName
        private const val SCHEDULE_ID = 0
        const val USE_WORK_MANAGER = false
    }
    /** Called when the activity is first created.  */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mModel = ViewModelProviders.of(this).get(PathMonitorViewModel::class.java)
        mModel.createDb(context!!)
        mModel.subscribeToDbChanges()
        mModel.getAll().observeForever{ elements ->
            for (element in elements) {
                if (!mMap.containsKey(element.url)) {
                    mMap[element.url] = ArrayList()
                }
                mMap[element.url]!!.add(element.path)
            }
        }

        scheduleJob(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setBackground(view, ColorDrawable(Color.BLACK))
        val wv: WebView = view.findViewById(R.id.tf_web_view)
        mLogic = WebViewLogic(context!!, mHandler, wv, object : WebViewLogic.Callback {
            override fun onReady(logic: WebViewLogic) {
                mModel.getAll().observe(this@MainFragment, Observer<List<WatchingElement>>{ result ->
                    val retList: List<Int> = result.map { element ->
                        GlobalScope.launch(Dispatchers.Main) {
                            logic.highlight(element.path)
                        }
                        element.id
                    }
                    Log.i(TAG, "result:$retList")
                })
            }

            override fun onElementClick(url: String, path: String, status: Boolean) {
                GlobalScope.launch {
                    if (status) {
                        mModel.add(url, path)
                    } else {
                        mModel.remove(url, path.replace(".watchdogSelected", ""))
                    }
                }
            }
        }, mOpenedUrl)
    }

    private fun openTargetSite(url: String) {
        mOpenedUrl = url
        mLogic.loadUrl(url)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scheduleJob(enable: Boolean) {
        if (USE_WORK_MANAGER) {
            val builder = Constraints.Builder()
                    //.setRequiresCharging(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                builder = builder.setRequiresDeviceIdle(true)
//            }
            val constraints = builder.build()
            val periodicWork = PeriodicWorkRequest.Builder(BackgroundCrawlerWorker::class.java, 5, TimeUnit.SECONDS)//任务执行周期12小时一次
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(context!!.packageName, ExistingPeriodicWorkPolicy.REPLACE, periodicWork)
        } else {
            val jobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            if (enable) {
                Log.d(TAG, "scheduleJob: $enable")
                val jobInfo = JobInfo.Builder(
                        SCHEDULE_ID, ComponentName(context!!.packageName, BackgroundCrawlerService::class.java.name))
                        .setPersisted(true)
                        .setPeriodic(AlarmManager.INTERVAL_FIFTEEN_MINUTES)
                        .build()
                jobScheduler.schedule(jobInfo)
            } else {
                Log.d(TAG, "scheduleJob: $enable")
                jobScheduler.cancel(SCHEDULE_ID)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val urlSettingItem = menu.findItem(R.id.action_url_setting)
        val editText: EditText = urlSettingItem.actionView.findViewById(R.id.actionEditText) as EditText
        editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val url = v?.text
                    openTargetSite(url.toString())
                    urlSettingItem.collapseActionView()
                }
                return actionId == EditorInfo.IME_ACTION_DONE
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_url_setting -> {
                val editText: EditText = item.actionView.findViewById(R.id.actionEditText) as EditText
                editText.onFocusChangeListener = object: View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val imm: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (hasFocus) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                    InputMethodManager.HIDE_IMPLICIT_ONLY)
                        } else {
                            imm.hideSoftInputFromWindow(editText.windowToken, 0)
                        }
                    }
                }
                editText.requestFocus()
                return true
            }
            R.id.action_block -> {
                mLogic.toggleHyperLink()
                Toast.makeText(context, "Toggle Hyperlink",
                        Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_reset -> {
                val future = ThreadManager.singleExecutor.submit(object : Callable<Any> {
                    @Throws(Exception::class)
                    override fun call(): Any {
                        mModel.clear(mLogic.targetUrl())
                        return "success"
                    }
                })
                if (future.get() == "success") {
                    Toast.makeText(context, "Clear watched items in ${mLogic.targetUrl()}",
                            Toast.LENGTH_SHORT).show()
                }
                return true
            }
            R.id.action_settings -> {
                findNavController().navigate(R.id.settings_fragment, null)
                return true
            }

            else -> {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
