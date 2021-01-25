package com.sabo.sabostorev2.EventBus;

import com.sabo.sabostorev2.Model.Item.ItemStoreModel;

public class OnCategoriesSelectedEvent {
    private boolean isSelected;
    private ItemStoreModel itemStoreModel;

    public OnCategoriesSelectedEvent(boolean isSelected, ItemStoreModel itemStoreModel) {
        this.isSelected = isSelected;
        this.itemStoreModel = itemStoreModel;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ItemStoreModel getItemStoreModel() {
        return itemStoreModel;
    }

    public void setItemStoreModel(ItemStoreModel itemStoreModel) {
        this.itemStoreModel = itemStoreModel;
    }
}
