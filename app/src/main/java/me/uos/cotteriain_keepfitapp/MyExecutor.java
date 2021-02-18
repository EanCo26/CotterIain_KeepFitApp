package me.uos.cotteriain_keepfitapp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.MainThread;
import androidx.core.content.ContextCompat;

public class MyExecutor {

    private static  final Object LOCK = new Object();
    private static MyExecutor sInstance;
    private final Executor diskIO;

    public MyExecutor(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static MyExecutor getInstance(){
        synchronized (LOCK){
            sInstance = new MyExecutor(Executors.newSingleThreadExecutor());
        }
        return sInstance;
    }

    public Executor getDiskIO() { return diskIO; }
}
