package com.sabo.sabostorev2.Model;

import com.google.gson.annotations.SerializedName;
import com.sabo.sabostorev2.Model.Item.ItemStoreModel;
import com.sabo.sabostorev2.Model.Item.ItemsModel;
import com.sabo.sabostorev2.Model.Order.OrderDetailsModel;
import com.sabo.sabostorev2.Model.Order.OrdersModel;

import java.util.List;

public class ResponseModel {
    @SerializedName("message") private String message;
    @SerializedName("code") private int code;
    @SerializedName("user") private UserModel user;
    @SerializedName("deliveryCost") private List<DeliveryCostModel> deliveryCost;
    @SerializedName("itemStore") private List<ItemStoreModel> itemStore;
    @SerializedName("items") private List<ItemsModel> items;
    @SerializedName("orders") private List<OrdersModel> orders;
    @SerializedName("orderDetails") private List<OrderDetailsModel> orderDetails;

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

    public List<OrdersModel> getOrders() {
        return orders;
    }

    public void setOrders(List<OrdersModel> orders) {
        this.orders = orders;
    }

    public List<OrderDetailsModel> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailsModel> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
