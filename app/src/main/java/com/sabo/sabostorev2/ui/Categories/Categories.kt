package com.sabo.sabostorev2.ui.Categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.ferfalk.simplesearchview.SimpleSearchViewListener
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Adapter.CategoriesAdapter
import com.sabo.sabostorev2.Adapter.ItemCategoriesSelectionAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.EventBus.OnLoadCategoriesEvent
import com.sabo.sabostorev2.Model.Item.ItemStoreModel
import com.sabo.sabostorev2.Model.Item.ItemsModel
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_categories.*
import maes.tech.intentanim.CustomIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Categories : AppCompatActivity() {

    private var mService: APIRequestData? = null
    private var searchList: List<ItemsModel> = ArrayList()
    private var sessionItemId = ""
    private var isCategoriesSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        mService = Common.getAPI()

        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Categories"

        rvItemCategoriesSelection.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        (rvItemCategoriesSelection.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        rvCategories.layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)


        simpleSearchView.setOnSearchViewListener(object : SimpleSearchViewListener() {
            override fun onSearchViewClosed() {
                onResume()
                tvEmptySearch.text = ""
                super.onSearchViewClosed()
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

        loadItemCategoriesSelection()
    }

    override fun onResume() {
        super.onResume()
        if (isCategoriesSelected) {
            if (sessionItemId == "all")
                loadCategories()
            else
                loadCategoriesByItemId(sessionItemId)
        } else
            loadCategories()
    }

    private fun loadCategories() {
        progressBar.visibility = View.VISIBLE
        mService!!.getItemSearch().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                progressBar.visibility = View.INVISIBLE
                if (code == 1) {
                    val resultList = response.body()!!.items
                    searchList = resultList
                    rvCategories.adapter = CategoriesAdapter(this@Categories, resultList)
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun loadCategoriesByItemId(sessionItemId: String) {
        progressBar.visibility = View.VISIBLE
        mService!!.getItems(sessionItemId).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                progressBar.visibility = View.INVISIBLE
                if (code == 1) {
                    val sessionResult = response.body()!!.items
                    searchList = sessionResult
                    rvCategories.adapter = CategoriesAdapter(this@Categories, sessionResult)
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun loadItemCategoriesSelection() {
        val itemList: ArrayList<ItemStoreModel> = ArrayList()
        val itemCategoriesSelection = Common.itemStoreModels
        if (itemCategoriesSelection!!.isNotEmpty()) {
            val item0 = ItemStoreModel()
            item0.id = "all"
            item0.name = "All"
            item0.description = "none"
            item0.image = "none"
            itemList.add(0, item0)
            itemList.addAll(itemCategoriesSelection)
            rvItemCategoriesSelection.adapter = ItemCategoriesSelectionAdapter(this, itemList)
        }
    }

    private fun onSearch(query: String) {
        progressBar.visibility = View.VISIBLE
        val resultSearch: ArrayList<ItemsModel> = ArrayList()
        for (items in searchList) {
            if (items.name!!.contains(query) || items.name!!.toLowerCase().contains(query) || (items.price.toString()).contains(query))
                resultSearch.add(items)
        }

        if (resultSearch.isEmpty())
            tvEmptySearch.text = "No results found for '$query'"
        else
            tvEmptySearch.text = ""

        rvCategories.adapter = CategoriesAdapter(this, resultSearch)
        progressBar.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadCategoriesBySession(event: OnLoadCategoriesEvent) {
        if (event.isSession) {
            sessionItemId = event.sessionItemId
            if (sessionItemId == "all")
                loadCategories()
            else {
                isCategoriesSelected = true
                loadCategoriesByItemId(sessionItemId)
            }
            tvEmptySearch.text = ""
            simpleSearchView.closeSearch()
            event.isSession = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        simpleSearchView.setMenuItem(menu.findItem(R.id.action_search))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
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
        CustomIntent.customType(this, Common.RTL)
    }
}