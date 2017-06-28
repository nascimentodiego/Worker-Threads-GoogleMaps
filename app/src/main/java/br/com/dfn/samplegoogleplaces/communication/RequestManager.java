package br.com.dfn.samplegoogleplaces.communication;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestManager {
    private static final RequestManager sInstance;

    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // A queue of Runnables for the download/GET pool
    private final BlockingQueue<Runnable> mDownloadWorkQueue;

    // A queue of RequestManager tasks. Tasks are handed to a ThreadPool.
    private final Queue<RequestTask> mRequestTaskWorkQueue;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mRequestThreadPool;

    private static CallbackRequest callbackRequest;

    private Handler mHandler;


    static {
        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        sInstance = new RequestManager();
    }

    private RequestManager() {

        mDownloadWorkQueue = new LinkedBlockingQueue<Runnable>();

        mRequestTaskWorkQueue = new LinkedBlockingQueue<RequestTask>();

        mRequestThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                    case DOWNLOAD_COMPLETE:
                        if (callbackRequest != null) {
                            byte[] bytes = (byte[]) inputMessage.obj;
                            callbackRequest.onResultRequest(bytes);
                        }
                        break;
                }
            }
        };

    }

    public static RequestManager getInstance() {

        return sInstance;
    }

    public void handleState(RequestTask requestTask, int state) {
        switch (state) {

            case DOWNLOAD_COMPLETE:
                Message completeMessage = mHandler.obtainMessage(state, requestTask.getByteBuffer());
                completeMessage.sendToTarget();
                break;
        }

    }

    static public RequestTask startRequest(String pUrl, CallbackRequest pCallbackRequest) {
        callbackRequest = pCallbackRequest;

        RequestTask downloadTask = sInstance.mRequestTaskWorkQueue.poll();

        if (null == downloadTask) {
            downloadTask = new RequestTask(pUrl);
        }

        sInstance.mRequestThreadPool.execute(downloadTask.getHTTPRunnable());
        Log.d("PoolThreads", "Pool: " + sInstance.mRequestThreadPool.getPoolSize());
        return downloadTask;
    }

    public interface CallbackRequest {
        void onResultRequest(byte[] str);
    }
}
