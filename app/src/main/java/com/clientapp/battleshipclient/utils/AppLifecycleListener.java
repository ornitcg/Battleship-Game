//package com.clientapp.battleshipclient.utils;
//
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.LifecycleObserver;
//import androidx.lifecycle.OnLifecycleEvent;
//import androidx.lifecycle.ProcessLifecycleOwner;
//
//public class AppLifecycleListener implements LifecycleObserver {
//
//    public AppLifecycleListener() {
//        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    public void onAppBackgrounded() {
//        // App is in the background
////        AudioUtils.pauseMusic();
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    public void onAppForegrounded() {
//        // App is in the foreground
//    }
//}
