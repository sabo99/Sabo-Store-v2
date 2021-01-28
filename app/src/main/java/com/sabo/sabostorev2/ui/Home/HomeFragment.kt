package com.sabo.sabostorev2.ui.Home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Adapter.HomeCategoriesAdapter
import com.sabo.sabostorev2.Adapter.MostPopularAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.ScaleCenterItemLayoutManager
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.Categories.Categories
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var homeViewModel: HomeViewModel? = null
    private var mService: APIRequestData? = null
    private lateinit var rvHomeCategories: RecyclerView
    private lateinit var rvMostPopular: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        mService = Common.getAPI()
        initViews(root)

        return root
    }

    private fun initViews(root: View) {
        rvHomeCategories = root.findViewById(R.id.rvHomeCategories)
        rvMostPopular = root.findViewById(R.id.rvMostPopular)
        rvHomeCategories.layoutManager = ScaleCenterItemLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        rvMostPopular.layoutManager = GridLayoutManager(root.context, 2)

        root.findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            startActivity(Intent(root.context, Categories::class.java))
            CustomIntent.customType(root.context, Common.LTR)
        }

        loadData()
    }

    private fun loadData() {
        /** Home Categories */
        mService!!.getItemStore().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                homeViewModel!!.setMutableLiveHomeCategories(response.body()!!.itemStore)
                Common.itemStoreModels = response.body()!!.itemStore
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
            }
        })

        /** Home Most Popular */
        mService!!.getMostPopular().enqueue(object : Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                homeViewModel!!.setMutableLivePopular(response.body()!!.items)
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
            }
        })

        Handler().postDelayed({
            homeViewModel!!.mutableLiveHomeCategories.observe(viewLifecycleOwner, { itemStoreModels ->
                rvHomeCategories.adapter = HomeCategoriesAdapter(requireContext(), itemStoreModels)
            })

            homeViewModel!!.mutableLivePopular.observe(viewLifecycleOwner, { itemsModels->
                rvMostPopular.adapter = MostPopularAdapter(requireContext(), itemsModels)
            })
        }, 1000)
    }

}