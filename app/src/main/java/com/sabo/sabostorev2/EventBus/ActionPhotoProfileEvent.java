package com.sabo.sabostorev2.EventBus;

public class ActionPhotoProfileEvent {
    boolean change, remove;

    public ActionPhotoProfileEvent(boolean remove, boolean change) {
        this.change = change;
        this.remove = remove;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }
}
