package com.muratkocoglu.inghubs.util;

public class AppUtil {
	
	private AppUtil() {
    }

    private static class LazyHolder {
        private static final AppUtil INSTANCE = new AppUtil();
    }

    public static AppUtil getInstance() {
        return LazyHolder.INSTANCE;
    }

}
