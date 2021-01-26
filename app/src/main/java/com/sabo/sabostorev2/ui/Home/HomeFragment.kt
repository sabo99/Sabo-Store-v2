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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Adapter.HomeCategoriesAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.ScaleCenterItemLayoutManager
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.Categories.Categories
import maes.tech.intentanim.CustomIntent
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var homeViewModel: HomeViewModel? = null
    private var mService: APIRequestData? = null
    private lateinit var rvHomeCategories: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        mService = Common.getAPI()
        initViews(root)

        return root
    }

    private fun initViews(root: View) {
        rvHomeCategories = root.findViewById(R.id.rvHomeCategories)
        rvHomeCategories.layoutManager = ScaleCenterItemLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)

        root.findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            startActivity(Intent(root.context, Categories::class.java))
            CustomIntent.customType(root.context, Common.LTR)
        }

        loadHomeCategories()
    }

    private fun loadHomeCategories() {
        mService!!.getItemStore().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                homeViewModel!!.setMutableLiveData(response.body()!!.itemStore)
                Common.itemStoreModels = response.body()!!.itemStore
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                val sweetD = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                sweetD.titleText = "Oops!"
                sweetD.contentText = t.message
                sweetD.show()
            }
        })

        Handler().postDelayed({
            homeViewModel!!.mutableLiveData.observe(viewLifecycleOwner, { itemStoreModels ->
                rvHomeCategories.adapter = HomeCategoriesAdapter(requireContext(), itemStoreModels)
            })
        }, 1000)
    }

}