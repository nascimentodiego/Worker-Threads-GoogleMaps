package br.com.dfn.samplegoogleplaces.communication;

public class RequestTask implements TaskRunnable.TaskRunnableMethods {

    Thread mThreadThis;
    String url;
    private Runnable mRunnable;
    private Thread mCurrentThread;
    byte[] mBuffer;

    private static RequestManager sRequestManager;

    RequestTask(String url) {
        this.url = url;
        mRunnable = new TaskRunnable(this);
        sRequestManager = RequestManager.getInstance();
    }

    public Thread getCurrentThread() {
        synchronized (sRequestManager) {
            return mCurrentThread;
        }
    }

    public void setCurrentThread(Thread thread) {
        synchronized (sRequestManager) {
            mCurrentThread = thread;
        }
    }

    public Runnable getHTTPRunnable() {
        return mRunnable;
    }

    @Override
    public void setDownloadThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    @Override
    public byte[] getByteBuffer() {
        return this.mBuffer;
    }

    @Override
    public void setByteBuffer(byte[] buffer) {
        this.mBuffer = buffer;
    }

    @Override
    public void handleDownloadState(int state) {
        int outState;
        // Converts the download state to the overall state
        switch (state) {
            case TaskRunnable.HTTP_STATE_COMPLETED:
                outState = RequestManager.DOWNLOAD_COMPLETE;
                break;
            case TaskRunnable.HTTP_STATE_FAILED:
                outState = RequestManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = RequestManager.DOWNLOAD_STARTED;
                break;
        }
        // Passes the state to the ThreadPool object.
        handleState(outState);
    }

    // Delegates handling the current state of the task to the PhotoManager object
    void handleState(int state) {
        sRequestManager.handleState(this, state);
    }

    @Override
    public String getUrl() {
        return url;
    }
}