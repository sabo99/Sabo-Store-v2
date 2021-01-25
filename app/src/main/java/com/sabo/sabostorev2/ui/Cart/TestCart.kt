package com.sabo.sabostorev2.ui.Cart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.Adapter.CartAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
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
import kotlinx.android.synthetic.main.activity_test_cart.*
import maes.tech.intentanim.CustomIntent


class TestCart : AppCompatActivity(), View.OnClickListener {

    private var compositeDisposable: CompositeDisposable? = null
    private var cartDataSource: CartDataSource? = null

    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_cart)


        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(RoomDBHost.getInstance(this).cartDAO())

        uid = Preferences.getUID(this)

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "TEST Cart"

        findViewById<Button>(R.id.btnAddCart).setOnClickListener(this)
        findViewById<Button>(R.id.btnNext).setOnClickListener(this)
        findViewById<FloatingActionButton>(R.id.fabClearAllCart).setOnClickListener(this)

        rvCart.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCart.setHasFixedSize(true)

    }

    override fun onResume() {
        super.onResume()
        loadCart()
        getTotalPrice()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnAddCart -> addToCart()
            R.id.btnNext -> {
                startActivity(Intent(this, TestOrder::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.fabClearAllCart -> clearAllCart()
        }
    }

    private fun loadCart() {
        compositeDisposable!!.add(cartDataSource!!.getAllCart(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ cartList ->
                    val cartAdapter = CartAdapter(this, cartList)
                    rvCart.adapter = cartAdapter
                    Log.d("cartList", cartList.toString())

                })
                { throwable: Throwable -> Log.d("loadCartList", throwable.message) })
    }

    private fun getTotalPrice() {
        cartDataSource!!.getTotalPrice(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Double> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(t: Double) {
                        val totalPrice = 0.0 + t
                        tvTotalPrice.text = "Total Price : $ ${Common.formatPriceUSDToDouble(totalPrice)}"
                        Common.totalPrice = totalPrice
                    }

                    override fun onError(e: Throwable) {
                        if (e.message!!.contains("empty")) {
                            tvTotalPrice.text = "Total Price : 0.0"
                            Common.totalPrice = 0.0
                        }
                    }
                })
    }

    private fun addToCart() {
        val name = etName.text.toString()
        val price = etPrice.text.toString()
        val qty = etQty.text.toString()

        if (name.isNullOrEmpty())
            etName.error = "Please Input Name!"
        else if (price.isNullOrEmpty())
            etPrice.error = "Please Input Price!"
        else if (qty.isNullOrEmpty())
            etQty.error = "Please Input Quantity!"
        else {
            btnAddCart.visibility = View.VISIBLE
            val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            sweetLoading.titleText = "Please wait..."
            sweetLoading.setCanceledOnTouchOutside(false)
            sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
            sweetLoading.show()

            Handler().postDelayed({
                clearForm()

                val cart = Cart()
                cart.itemId = System.currentTimeMillis().toString()
                cart.uid = uid
                cart.itemName = name
                cart.itemPrice = Common.formatPriceUSDToDouble(price.toDouble() / Common.ratesIDR)
                cart.itemQuantity = qty.toInt()
                cart.itemImage = "Default.png"

                compositeDisposable!!.add(cartDataSource!!.insertOrReplaceAll(cart)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            btnAddCart.visibility = View.VISIBLE
                            sweetLoading.dismissWithAnimation()
                            getTotalPrice()
                        })
                        { throwable: Throwable ->
                            Log.d("cart", throwable.message)
                            sweetLoading.titleText = "Oops!"
                            sweetLoading.contentText = throwable.message
                            sweetLoading.setConfirmClickListener {
                                sweetLoading.dismissWithAnimation()
                            }
                            sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                        })
            }, 1000)

        }
    }

    private fun clearForm() {
        etName.setText("")
        etPrice.setText("")
        etQty.setText("")
        etName.clearFocus()
        etPrice.clearFocus()
        etQty.clearFocus()
        etName.requestFocus()
    }

    private fun clearAllCart() {
        val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.titleText = "Please wait..."
        sweetLoading.setCanceledOnTouchOutside(false)
        sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
        sweetLoading.show()

        cartDataSource!!.clearAllCart(uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Integer> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(t: Integer) {
                        sweetLoading.dismissWithAnimation()
                        tvTotalPrice.text = "Total Price :"
                        Common.totalPrice = 0.0
                    }

                    override fun onError(e: Throwable) {
                        sweetLoading.dismissWithAnimation()
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