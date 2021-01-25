package com.sabo.sabostorev2.ui.Search

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.ferfalk.simplesearchview.SimpleSearchViewListener
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Adapter.SearchAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.Item.ItemsModel
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_search.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {

    private var mService: APIRequestData? = null
    private var searchList: List<ItemsModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Sabo Store"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val w = window
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        rvSearch.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        simpleSearchView.setOnSearchViewListener(object : SimpleSearchViewListener() {
            override fun onSearchViewClosedAnimation() {
                loadSearch()
                tvEmptySearch.text = ""
                super.onSearchViewClosedAnimation()
            }
        })

        simpleSearchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                onSearch(newText)
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadSearch()
    }

    private fun loadSearch() {
        progressBar.visibility = View.VISIBLE
        mService!!.getItemSearch().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                Handler().postDelayed({
                    progressBar.visibility = View.INVISIBLE
                    if (code == 1) {
                        val result = SearchAdapter(this@SearchActivity, response.body()!!.items)
                        rvSearch.adapter = result
                        searchList = response.body()!!.items
                    }
                }, 500)

            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun onSearch(query: String) {
        progressBar.visibility = View.VISIBLE
        val resultSearch: ArrayList<ItemsModel> = ArrayList()
        for (items: ItemsModel in searchList) {
            if (items.name!!.contains(query) || items.name!!.toLowerCase().contains(query) || (items.price.toString()).contains(query))
                resultSearch.add(items)
        }

        if (resultSearch.isEmpty())
            tvEmptySearch.text = "No results found for '$query'"
        else
            tvEmptySearch.text = ""

        rvSearch.adapter = SearchAdapter(this, resultSearch)
        progressBar.visibility = View.INVISIBLE

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        simpleSearchView.setMenuItem(menu.findItem(R.id.action_search))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (simpleSearchView.onBackPressed())
            return
        super.onBackPressed()
    }


    override fun finish() {
        super.finish()
        simpleSearchView.closeSearch()
        //CustomIntent.customType(this, Common.UTB)
    }
}