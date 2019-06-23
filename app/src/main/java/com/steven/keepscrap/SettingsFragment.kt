package com.steven.keepscrap

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.steven.keepscrap.db.WatchingElement
import com.steven.keepscrap.nestedlist.adapter.ParentAdapter
import com.steven.keepscrap.nestedlist.model.Child
import com.steven.keepscrap.nestedlist.model.ParentChild
import java.util.concurrent.Callable


class SettingsFragment : Fragment() {
    private lateinit var mModel: PathMonitorViewModel
    private var mWatchingListView: RecyclerView? = null
    private val mMap = HashMap<String, ArrayList<WatchingElement>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tf_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        mModel = ViewModelProviders.of(this).get(PathMonitorViewModel::class.java)
        mModel.createDb(context!!)
        mModel.subscribeToDbChanges()
        mModel.getAll().observeForever{ elements ->
            for (element in elements) {
                if (!mMap.containsKey(element.url)) {
                    mMap[element.url] = ArrayList()
                }
                mMap[element.url]!!.add(element)
                if (mWatchingListView != null) {
                    mWatchingListView?.adapter = ParentAdapter(context, createData())
                }
            }
        }
    }

    private fun createData(): ArrayList<ParentChild> {
        val parentChildObj: ArrayList<ParentChild> = ArrayList()

        mMap.keys.forEach {
            val list: ArrayList<WatchingElement> = mMap[it]!!
            val pathList: List<Child> = list.map { element ->  Child(element.path) }
            (pathList as ArrayList).add(0, Child(list[0].url))
            val pc = ParentChild()
            pc.child = pathList
            parentChildObj.add(pc)
        }

        return parentChildObj
    }

    companion object {
        var TAG: String = this.javaClass.simpleName
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mWatchingListView = view.findViewById(R.id.watching_list)


        val manager = LinearLayoutManager(context)
        //manager.setOrientation(LinearLayoutManager.VERTICAL)
        mWatchingListView?.layoutManager = manager
        mWatchingListView?.setHasFixedSize(true)

        mWatchingListView?.adapter = ParentAdapter(context, createData())
    }
}
