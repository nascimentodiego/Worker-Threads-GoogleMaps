package br.com.dfn.samplegoogleplaces.communication;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Domdi on 27/06/2017.
 */

public class TaskRunnable implements Runnable {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = "TaskRunnable";

    // Constants for indicating the state of the download
    static final int HTTP_STATE_FAILED = -1;
    static final int HTTP_STATE_STARTED = 0;
    static final int HTTP_STATE_COMPLETED = 1;

    private final String mUrl;

    // Defines a field that contains the calling object of type PhotoTask.
    final TaskRunnableMethods mTask;

    TaskRunnable(TaskRunnableMethods task) {
        mTask = task;
        mUrl = task.getUrl();
    }

    interface TaskRunnableMethods {
        void setDownloadThread(Thread currentThread);

        byte[] getByteBuffer();

        void setByteBuffer(byte[] buffer);

        void handleDownloadState(int state);

        String getUrl();
    }


    @Override
    public void run() {
         /*
         * Stores the current Thread in the the PhotoTask instance, so that the instance
         * can interrupt the Thread.
         */
        mTask.setDownloadThread(Thread.currentThread());

        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        try {
            isInterrupted();
            mTask.handleDownloadState(HTTP_STATE_STARTED);

            URL url = new URL(mUrl);
            InputStream byteStream = null;
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(1500);

            isInterrupted();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                byteStream = connection.getInputStream();

                if (byteStream == null) {
                    throw new Exception("Fail to read byteStream");
                }
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int lidos;
                while ((lidos = byteStream.read(bytes)) > 0) {
                    isInterrupted();
                    baos.write(bytes, 0, lidos);
                }

                String str = new String(baos.toByteArray());
                mTask.setByteBuffer(baos.toByteArray());
                Log.d("TASKRUNNABLE", str);

                mTask.handleDownloadState(HTTP_STATE_COMPLETED);

            } else {
                mTask.handleDownloadState(HTTP_STATE_FAILED);
                throw new Exception("Fail");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void isInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}