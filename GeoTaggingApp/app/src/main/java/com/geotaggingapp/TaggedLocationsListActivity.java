package com.geotaggingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.geotaggingapp.adapter.RecyclerViewAdapter;
import com.geotaggingapp.interfaces.OnItemClickListener;
import com.geotaggingapp.model.GeoTagInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class TaggedLocationsListActivity extends AppCompatActivity {

    private LatLng latLng;
    private RecyclerView recyclerView;
    private ArrayList<GeoTagInfo> taggedLocationInfo;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagged_locations_list);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getBundleExtra("bundle");
            latLng = bundle.getParcelable("latLng");
            taggedLocationInfo = (ArrayList<GeoTagInfo>) bundle.getSerializable("taggedLocationsList");
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, taggedLocationInfo, latLng, new OnItemClickListener() {
            @Override
            public void onItemClick(double latitude, double longitude, byte[] taggedImage) {

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("latLng", new LatLng(latitude, longitude));
                bundle.putByteArray("image", taggedImage);
                intent.putExtra("bundle", bundle);
                setResult(MainActivity.TAGGED_ACTIVITY_REQ_CODE, intent);
                finish();
            }
        });
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
