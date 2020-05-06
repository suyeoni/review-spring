package com.example.springpractice.spel;

public class MethodProvider {
//    public void update(String userId, String key, long value) {
//        System.out.println("[" + userId + "] " + key + " : (long)" + value);
//    }

    public void update(String userId, String key, Object value) {
        System.out.println("[" + userId + "] " + key + " : (Object)" + value);
    }

    public void incr(String userId, String key, long incrementAmount) {
        System.out.println("[" + userId + "] " + key + ", increment " + incrementAmount);
    }
}
