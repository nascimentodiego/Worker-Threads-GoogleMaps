package br.com.dfn.samplegoogleplaces;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.zzp;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import br.com.dfn.samplegoogleplaces.communication.Parser;
import br.com.dfn.samplegoogleplaces.communication.RequestManager;
import br.com.dfn.samplegoogleplaces.model.Place;
import br.com.dfn.samplegoogleplaces.util.PermissionUtil;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        RequestManager.CallbackRequest, OnRequestPermissionsResultCallback, OnSuccessListener<Location>, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private String radius = "5000";
    private LatLng myLastKnowLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (PermissionUtil.hasPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                this, PermissionUtil.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)) {
            setMyLocation();
        }


        mMap.setOnMarkerClickListener(this);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (null == myLastKnowLocation) {
                    return;
                }

               /* CameraPosition cameraPosition = mMap.getCameraPosition();
                if (cameraPosition.zoom < 10.0) {
                    doRequest(myLastKnowLocation, "5000");
                } else if (cameraPosition.zoom > 15.0) {
                    doRequest(myLastKnowLocation, "2000");
                } else if (cameraPosition.zoom > 18.0) {
                    doRequest(myLastKnowLocation, "1000");
                }*/
            }
        });
    }


    private void setMyLocation() {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(false);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, this);
        }
    }

    @Override
    public void onResultRequest(byte[] bytes) {
        synchronized (mMap) {
            mMap.clear();
            List<Place> places = Parser.parseBytesToObject(bytes);
            for (final Place place : places) {
                final LatLng mapCenter = new LatLng(place.getLat(), place.getLng());
                BitmapDescriptor icon = null;

                if (place.getType().equals(Place.TYPE_AIRPORT)) {
                    icon = BitmapDescriptorFactory.fromBitmap(getPlaceIcon(R.drawable.place_airport));
                } else if (place.getType().equals(Place.TYPE_NIGHT_CLUB)) {
                    icon = BitmapDescriptorFactory.fromBitmap(getPlaceIcon(R.drawable.place_party));
                } else if (place.getType().equals(Place.TYPE_RESTAURANT)) {
                    icon = BitmapDescriptorFactory.fromBitmap(getPlaceIcon(R.drawable.place_restaurant));
                } else if (place.getType().equals(Place.TYPE_SHOPPING)) {
                    icon = BitmapDescriptorFactory.fromBitmap(getPlaceIcon(R.drawable.place_shopping));
                } else {
                    icon = BitmapDescriptorFactory.fromBitmap(getPlaceIcon(R.drawable.place_market));
                }

                mMap.addMarker(new MarkerOptions()
                        .icon(icon)
                        .position(mapCenter)).setTag(place);
            }


            //Add My Location
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(getPlaceIcon(R.drawable.profile_active));
            mMap.addMarker(new MarkerOptions()
                    .icon(icon)
                    .position(myLastKnowLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLastKnowLocation, 15));

            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(myLastKnowLocation)
                    .zoom(13)
                    .bearing(90)
                    .build();

            // Animate the change in camera view over 2 seconds
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }
    }

    public Bitmap getPlaceIcon(int resourceId) {
        int height = 80;
        int width = 80;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(resourceId);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocation();
                }
                return;
            }
        }
    }

    public void doRequest(LatLng myLastKnowLocation, String radius) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + myLastKnowLocation.latitude + "," + myLastKnowLocation.longitude + "&radius=" + radius
                + "&key=AIzaSyBK5aGJiR7TH5B599cdoM5ebcBdWX-9W7U&types=restaurant&types=airport";
        RequestManager.startRequest(url, this);
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {

            double lat = location.getLatitude();
            double lng = location.getLongitude();
            myLastKnowLocation = new LatLng(lat, lng);
            doRequest(myLastKnowLocation, radius);


        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, "marker: " + ((Place)marker.getTag()).getName(), Toast.LENGTH_SHORT).show();
        return false;
    }

}
