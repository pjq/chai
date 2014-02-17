package me.pjq.chai;

public enum ApplicationConfig implements MyApplicationConfigInterface {
    INSTANCE;

    public boolean DEBUG() {
        return false;
    }

    public boolean DEBUG_LOG() {
        return false;
    }

    public boolean API_DEV() {
        return false;
    }

    public boolean UPDATE_WORDLIST_FROM_SERVER() {
        return true;
    }
}
