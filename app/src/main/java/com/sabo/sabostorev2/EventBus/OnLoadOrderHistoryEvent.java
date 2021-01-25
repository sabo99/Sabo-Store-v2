package com.sabo.sabostorev2.EventBus;

public class OnLoadOrderHistoryEvent {
    private boolean isLoad;

    public OnLoadOrderHistoryEvent(boolean isLoad) {
        this.isLoad = isLoad;
    }

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }
}
