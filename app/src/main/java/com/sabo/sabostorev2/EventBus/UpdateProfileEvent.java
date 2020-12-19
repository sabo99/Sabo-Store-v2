package com.sabo.sabostorev2.EventBus;

public class UpdateProfileEvent {
    boolean updated;

    public UpdateProfileEvent(boolean updated) {
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
