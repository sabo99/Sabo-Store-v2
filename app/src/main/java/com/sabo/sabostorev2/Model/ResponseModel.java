package com.sabo.sabostorev2.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {
    @SerializedName("message") private String message;
    @SerializedName("code") private int code;
    @SerializedName("user") private UserModel user;
    @SerializedName("deliveryCost") private List<DeliveryCostModel> deliveryCost;
    @SerializedName("itemStore") private List<ItemStoreModel> itemStore;
    @SerializedName("items") private List<ItemsModel> items;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public List<DeliveryCostModel> getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(List<DeliveryCostModel> deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public List<ItemStoreModel> getItemStore() {
        return itemStore;
    }

    public void setItemStore(List<ItemStoreModel> itemStore) {
        this.itemStore = itemStore;
    }

    public List<ItemsModel> getItems() {
        return items;
    }

    public void setItems(List<ItemsModel> items) {
        this.items = items;
    }
}
