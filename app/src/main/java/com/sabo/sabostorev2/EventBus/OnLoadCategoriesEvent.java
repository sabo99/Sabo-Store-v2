package com.sabo.sabostorev2.EventBus;

public class OnLoadCategoriesEvent {
    private boolean isSession;
    private String sessionItemId;

    public OnLoadCategoriesEvent(boolean isSession, String sessionItemId) {
        this.isSession = isSession;
        this.sessionItemId = sessionItemId;
    }

    public boolean isSession() {
        return isSession;
    }

    public void setSession(boolean session) {
        isSession = session;
    }

    public String getSessionItemId() {
        return sessionItemId;
    }

    public void setSessionItemId(String sessionItemId) {
        this.sessionItemId = sessionItemId;
    }
}
