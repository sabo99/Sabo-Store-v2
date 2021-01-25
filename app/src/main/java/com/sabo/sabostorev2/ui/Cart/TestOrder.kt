package com.sabo.sabostorev2.ui.Cart

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Adapter.CartAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.Cart.Cart
import com.sabo.sabostorev2.RoomDB.Cart.CartDataSource
import com.sabo.sabostorev2.RoomDB.Cart.LocalCartDataSource
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_test_order.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestOrder : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var cartDataSource: CartDataSource? = null
    private var cartList: List<Cart> = ArrayList()
    private var uid = ""
    private var totalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_order)

        mService = Common.getAPI()
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(RoomDBHost.getInstance(this).cartDAO())

        uid = Preferences.getUID(this)
        totalPrice = Common.totalPrice
        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "TEST Order"

        findViewById<FloatingActionButton>(R.id.fabOrder).setOnClickListener(this)

        tvTotalPrice.text = "Total Price : $ ${Common.formatPriceUSDToDouble(Common.totalPrice)}"
        rvOrder.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvOrder.setHasFixedSize(true)

        loadOrder()
    }

    private fun loadOrder() {
        compositeDisposable!!.add(cartDataSource!!.getAllCart(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { rCartList ->
                    cartList = rCartList
                    rvOrder.adapter = CartAdapter(this, cartList)
                })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.fabOrder -> {
                if (cartList.isNotEmpty())
                    orderItems()
                else
                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun orderItems() {
        val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.titleText = "Please wait..."
        sweetLoading.setCanceledOnTouchOutside(false)
        sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
        sweetLoading.show()

        val orderID = System.currentTimeMillis().toString()

        /** Order */
        mService!!.order(orderID, uid, totalPrice).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                if (code == 1) {

                    /** Order Details */
                    if (cartList.size > 1) {
                        for (x in cartList.indices) {
                            mService!!.orderDetail(orderID, cartList[x].itemName, cartList[x].itemQuantity, cartList[x].itemPrice, cartList[x].itemImage).enqueue(object : Callback<ResponseModel> {
                                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                                    val code = response.body()!!.code
                                    if (code == 1) {
                                        /** Clear All Cart */
                                        clearAllCart()

                                        sweetLoading.titleText = "Success!"
                                        sweetLoading.contentText = response.body()!!.message
                                        sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                                        sweetLoading.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    } else {
                                        sweetLoading.titleText = "Oops!"
                                        sweetLoading.contentText = response.body()!!.message
                                        sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                                        sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                                    }
                                }

                                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                                    sweetLoading.titleText = "Oops!"
                                    sweetLoading.contentText = t.message
                                    sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                                    sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                                }
                            })
                        }
                    } else {
                        mService!!.orderDetail(orderID, cartList[0].itemName, cartList[0].itemQuantity, cartList[0].itemPrice, cartList[0].itemImage).enqueue(object : Callback<ResponseModel> {
                            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                                val code = response.body()!!.code
                                if (code == 1) {
                                    /** Clear All Cart */
                                    clearAllCart()

                                    sweetLoading.titleText = "Success!"
                                    sweetLoading.contentText = response.body()!!.message
                                    sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                                    sweetLoading.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                }
                            }

                            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                                sweetLoading.titleText = "Oops!"
                                sweetLoading.contentText = t.message
                                sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                                sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                            }
                        })
                    }
                } else {
                    sweetLoading.titleText = "Oops!"
                    sweetLoading.contentText = response.body()!!.message
                    sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                    sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                sweetLoading.titleText = "Oops!"
                sweetLoading.contentText = t.message
                sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
            }
        })


//        if (cartList.size > 1) {
//            for (x in cartList.indices) {
//                Log.d("cartList", "ItemID : " + cartList!![x].itemId +
//                        " | ItemName : " + cartList!![x].itemName +
//                        " | ItemQty : " + cartList!![x].itemQty +
//                        " | ItemPrice : " + cartList!![x].itemPrice)
//            }
//        } else {
//            Log.d("cartList", "ItemID : " + cartList!![0].itemId +
//                    " | ItemName : " + cartList!![0].itemName +
//                    " | ItemQty : " + cartList!![0].itemQty +
//                    " | ItemPrice : " + cartList!![0].itemPrice)
//        }
    }

    private fun clearAllCart() {
        cartDataSource!!.clearAllCart(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Integer> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(t: Integer) {
                        Common.totalPrice = 0.0
                        tvTotalPrice.text = "Total Price : 0.0"
                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable!!.clear()
    }
}