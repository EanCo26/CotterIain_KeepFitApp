package me.uos.cotteriain_keepfitapp.General;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyExecutor {

    private static  final Object LOCK = new Object();
    private static MyExecutor sInstance;
    private final Executor diskIO;

    /**
     * A process for running database queries (DAO) off the main thread
     * @param diskIO
     */
    public MyExecutor(Executor diskIO) {
        this.diskIO = diskIO;
    }

    /**
     * @return singleton of Executor to run
     */
    public static MyExecutor getInstance(){
        synchronized (LOCK){
            sInstance = new MyExecutor(Executors.newSingleThreadExecutor());
        }
        return sInstance;
    }

    public Executor getDiskIO() { return diskIO; }
}
