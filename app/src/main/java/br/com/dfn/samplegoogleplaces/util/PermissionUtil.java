package br.com.dfn.samplegoogleplaces.util;


import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class PermissionUtil {
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    public static boolean hasPermission(String[] permission, AppCompatActivity activity, int requestCode) {
        boolean result = true;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity, permission[0])
                != PackageManager.PERMISSION_GRANTED) {
            result = false;

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        permission, requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        return result;
    }
}
