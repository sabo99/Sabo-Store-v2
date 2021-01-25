package com.sabo.sabostorev2.ui.Home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sabo.sabostorev2.Model.Item.ItemStoreModel

class HomeViewModel : ViewModel() {
    val mutableLiveData: MutableLiveData<List<ItemStoreModel>> = MutableLiveData()

    fun setMutableLiveData(itemStoreModelList: List<ItemStoreModel>) {
        mutableLiveData.value = itemStoreModelList
    }

}