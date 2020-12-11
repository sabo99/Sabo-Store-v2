package com.sabo.sabostorev2.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sabo.sabostorev2.Model.ItemStoreModel;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<ItemStoreModel>> mutableLiveData;

    public HomeViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<ItemStoreModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    public void setMutableLiveData(List<ItemStoreModel> itemStoreModelList) {
        mutableLiveData.setValue(itemStoreModelList);
    }
}