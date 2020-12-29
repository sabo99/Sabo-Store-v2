package com.sabo.sabostorev2.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sabo.sabostorev2.API.APIRequestData;
import com.sabo.sabostorev2.Adapter.CategoriesAdapter;
import com.sabo.sabostorev2.Common.Common;
import com.sabo.sabostorev2.Common.ScaleCenterItemLayoutManager;
import com.sabo.sabostorev2.Model.ResponseModel;
import com.sabo.sabostorev2.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private APIRequestData mService;
    private HomeViewModel homeViewModel;
    private RecyclerView rvCategories;
    private CategoriesAdapter categoriesAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mService = Common.getAPI();
        initViews(root);

        return root;
    }

    private void initViews(View root) {
        rvCategories = root.findViewById(R.id.rvCategories);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        ScaleCenterItemLayoutManager layoutManager = new ScaleCenterItemLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(layoutManager);
        loadCategories();
    }

    private void loadCategories() {
        mService.getItemStore().enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                homeViewModel.setMutableLiveData(response.body().getItemStore());
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("itemStore", t.getMessage());
            }
        });

        new Handler().postDelayed(() -> {
            homeViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), itemStoreModels -> {
                categoriesAdapter = new CategoriesAdapter(getContext(), itemStoreModels);
                rvCategories.setAdapter(categoriesAdapter);
            });
        },1000);

    }
}