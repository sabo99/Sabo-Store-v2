package com.sabo.sabostorev2.ui.Home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sabo.sabostorev2.Model.Item.ItemStoreModel
import com.sabo.sabostorev2.Model.Item.ItemsModel

class HomeViewModel : ViewModel() {
    val mutableLiveHomeCategories: MutableLiveData<List<ItemStoreModel>> = MutableLiveData()
    val mutableLivePopular : MutableLiveData<List<ItemsModel>> = MutableLiveData()

    fun setMutableLiveHomeCategories(itemStoreModelList: List<ItemStoreModel>) {
        mutableLiveHomeCategories.value = itemStoreModelList
    }

    fun setMutableLivePopular(itemsModelList: List<ItemsModel>){
        mutableLivePopular.value = itemsModelList
    }
}