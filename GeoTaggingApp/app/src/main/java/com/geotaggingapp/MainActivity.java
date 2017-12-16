package com.geotaggingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.geotaggingapp.dialogs.AlertDialogWithAction;
import com.geotaggingapp.model.GeoTagInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mGoogleMap;
    private Context mContext;
    public static final int PERMISSION_CAMERA = 10;
    public static final int CAMERA_REQUEST_CODE = 11;
    private AlertDialogWithAction mAlertDialogWithAction;
    private LatLng latLng;
    private ArrayList<GeoTagInfo> taggedLocationsInfoList = new ArrayList<>();
    public static final int TAGGED_ACTIVITY_REQ_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mAlertDialogWithAction = new AlertDialogWithAction(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        // Set Zoom and rotate controls on Map
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        // Apply Click Listeners on Map
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        /**
         * Camera will be shown to user when he will long press any location of Google Maps.
         * Camera captured image along with the location details will be shown to user as marker on Map.
         */
        this.latLng = latLng;
        checkCameraPermission();
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    /**
     * Function to check if the camera permission is given to this app or not
     * Ask for permission if not given else Launch camera.
     */
    private void checkCameraPermission() {

        if(!(ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {
            openCamera();
        }
    }

    /**
     * Launch camera intent to capture images.
     */
    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * Function to get the address of the selected location
     * @param latLng LatLng of the selected location
     * @param bitmap bitmap of the captured image
     */
    private void getLocationAddress(LatLng latLng, Bitmap bitmap) {

        Geocoder geocoder = new Geocoder(this);
        List<Address> list;
        try {
            list = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, 1);
            if(list != null && list.size() != 0) {
                Address address = list.get(0);

                MarkerOptions options = new MarkerOptions()
                        .title(address.getAddressLine(0))
                        .position(new LatLng(latLng.latitude,
                                latLng.longitude));

                Marker marker = mGoogleMap.addMarker(options);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                addInfoInList(address.getAddressLine(0), latLng, bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to add the Location Info in the list of all tagged locations
     * @param address address of the tagged location
     * @param latLng LatLng of the tagged location
     * @param bitmap Bitmap of the captured image of tagged locations
     */
    private void addInfoInList(String address, LatLng latLng, Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        GeoTagInfo geoTagInfo = new GeoTagInfo(byteArray, latLng.latitude, latLng.longitude, address);
        taggedLocationsInfoList.add(geoTagInfo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSION_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    openCamera();

                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                            Manifest.permission.CAMERA)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.
                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.CAMERA,
                                PERMISSION_CAMERA);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE :

                if(resultCode == RESULT_OK){

                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    getLocationAddress(latLng, bitmap);

                }else if (resultCode != RESULT_CANCELED) {
                    // Image Capture has been canceled.
                }
                break;

            // Show the marker when any listitem is being clicked.
            case TAGGED_ACTIVITY_REQ_CODE:
                if(resultCode == TAGGED_ACTIVITY_REQ_CODE) {
                    Bundle markerInfo = data.getBundleExtra("bundle");
                    LatLng latLng = markerInfo.getParcelable("latLng");
                    byte[] image = markerInfo.getByteArray("image");

                    if(latLng != null) {
                        MarkerOptions options = new MarkerOptions()
                                .title("Clicked Marker")
                                .position(new LatLng(latLng.latitude,
                                        latLng.longitude));

                        Marker marker = mGoogleMap.addMarker(options);
                        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp));

                    }

                }
        }
    }

    /**
     * Launch activity when a marker position clicked
     * pass all the tagged locations list to the activity
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        LatLng latLng = marker.getPosition();

        Intent intent = new Intent(this, TaggedLocationsListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("taggedLocationsList", taggedLocationsInfoList);
        bundle.putParcelable("latLng", latLng);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, TAGGED_ACTIVITY_REQ_CODE);
        return false;
    }
}
