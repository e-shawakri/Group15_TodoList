package com.group15.todoList;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.group15.todoList.model.TodoItem;

import java.util.*;

public class ItemDetailsActivity extends AppCompatActivity implements OnClickListener, OnMapReadyCallback {

	protected static final String logger = ItemDetailsActivity.class
			.getSimpleName();

	public static final String ARG_ITEM_OBJECT = "itemObject";

	public static final int RESPONSE_ITEM_UPDATED = 1;

	public static final int RESPONSE_ITEM_DELETED = 2;

	private GoogleMap map;
	LocationRequest mLocationRequest;
	Location mLastLocation;
	Marker mCurrLocationMarker;
	FusedLocationProviderClient mFusedLocationClient;

	private TodoItem item;

	private EditText itemName;
	private EditText itemDescription;
	private CheckBox itemFavourite;
	private CalendarView itemDate;
	private NumberPicker itemImportant;
	private CheckBox itemDone;
	private LatLng itemCoords;

	private Button saveButton;
	private Button deleteButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemview);


		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		try {
			mapFragment.getMapAsync(this);
			this.itemName = (EditText) findViewById(R.id.item_name);
			this.itemDescription = (EditText) findViewById(R.id.item_description);
			this.itemFavourite = (CheckBox) findViewById(R.id.item_favourite);
			this.itemDate = (CalendarView) findViewById(R.id.item_date);
			this.itemImportant = (NumberPicker) findViewById(R.id.item_important);
			this.itemDone = (CheckBox) findViewById(R.id.item_done);
			itemImportant.setMinValue(0);
			itemImportant.setMaxValue(10);

			this.saveButton = (Button) findViewById(R.id.saveButton);
			this.deleteButton = (Button) findViewById(R.id.deleteButton);
			this.item = (TodoItem) getIntent().getSerializableExtra(ARG_ITEM_OBJECT);

			if (this.item != null) {
				setTitle("Todo Details");
				itemName.setText(item.getName());
				itemDescription.setText(item.getDescription());
				itemDate.setDate(item.getDate());
				itemFavourite.setChecked(item.isFavourite());
				itemImportant.setValue(item.getImportance());
				itemDone.setChecked(item.isDone());
			} else {
				setTitle("New Todo");
				deleteButton.setVisibility(View.GONE);
				this.item = new TodoItem(-1, "", "", false, new Date().getTime(),
						0, false);
			}

			this.saveButton.setOnClickListener(this);
			this.deleteButton.setOnClickListener(this);
			itemDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

				@Override
				public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

					Calendar c = Calendar.getInstance();

					c.set(year, month, day);

					itemDate.setDate(c.getTimeInMillis());
				}
			});
		} catch (Exception e) {
			String err = "got exception: " + e;
			Log.e(logger, err, e);
			((DataAccessRemoteApplication) getApplication()).reportError(this,
					err);
		}

	}



	protected boolean validateFields() {
		Toast toast = null;
		boolean isValid = true;

		if (TextUtils.isEmpty(this.itemName.getText().toString())) {
			toast = Toast.makeText(this, "Name is required field", Toast.LENGTH_SHORT);
			isValid = false;
		}

		if (TextUtils.isEmpty(this.itemDescription.getText().toString())) {
			toast = Toast.makeText(this, "Description is required field ", Toast.LENGTH_SHORT);
			isValid = false;
		}

		if (toast != null) {
			toast.show();
		}

		return isValid;
	}

	protected void processItemSave() {
		if (!this.validateFields()) {
			return;
		}

		this.item.setName(this.itemName.getText().toString());
		this.item.setDescription(this.itemDescription.getText().toString());
		this.item.setFavourite(this.itemFavourite.isChecked());
		this.item.setDate(this.itemDate.getDate());
		this.item.setDone(this.itemDone.isChecked());
		this.item.setImportance(this.itemImportant.getValue());

		Intent returnIntent = new Intent();

		returnIntent.putExtra(ARG_ITEM_OBJECT, this.item);

		setResult(RESPONSE_ITEM_UPDATED, returnIntent);

		finish();
	}
	protected void processItemDelete() {

		Intent returnIntent = new Intent();

		returnIntent.putExtra(ARG_ITEM_OBJECT, this.item);

		setResult(RESPONSE_ITEM_DELETED, returnIntent);

		finish();
	}

	@Override
	public void onClick(View view) {
		if (view == this.saveButton) {
			processItemSave();
		} else if (view == this.deleteButton) {
			processItemDelete();
		} else {
			Log.w(logger, "got onClick() on view where it will not be handled: " + view);
		}
	}

	private void requestLocation() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(120000);
		mLocationRequest.setFastestInterval(120000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED) {
				mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
				map.setMyLocationEnabled(true);
			} else {
				checkLocationPermission();
			}
		}
		else {
			mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
			map.setMyLocationEnabled(true);
		}
	}

	public LatLng generateRandomLocation() {
		LatLng point = new LatLng(52.411509, 12.539004);
		Random random = new Random();

		int radius = 5;

		double angle = random.nextDouble() * Math.PI * 2;
		double randomRadius = radius*random.nextDouble();

		double relativeX = Math.cos(angle) * randomRadius;
		double relativeY = Math.sin(angle) * randomRadius;

		return new LatLng(point.latitude + relativeX, point.longitude + relativeY);
	}


	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

//		if (item.getCoords().isPresent()) {
//			this.itemCoords = item.getCoords().get();
//		}
		this.itemCoords = this.generateRandomLocation();

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(this.itemCoords);
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

		mCurrLocationMarker = map.addMarker(markerOptions);

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(this.itemCoords, 14));

		if (this.itemCoords != null) {
			if (mCurrLocationMarker != null) {
				mCurrLocationMarker.setPosition(this.itemCoords);
			} else {
				MarkerOptions markerOpts = new MarkerOptions();
				markerOpts.position(this.itemCoords);
				markerOpts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

				mCurrLocationMarker = map.addMarker(markerOpts);
			}
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(this.itemCoords, 14));
		} else {
			requestLocation();
		}
	}


	LocationCallback mLocationCallback = new LocationCallback() {
		@Override
		public void onLocationResult(LocationResult locationResult) {
			List<Location> locationList = locationResult.getLocations();
			if (locationList.size() > 0) {
				Location location = locationList.get(locationList.size() - 1);
				Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
				mLastLocation = location;
				if (mCurrLocationMarker != null) {
					mCurrLocationMarker.remove();
				}

				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
				mCurrLocationMarker = map.addMarker(markerOptions);

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
			}
		}
	};

	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	private void checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				new AlertDialog.Builder(this)
						.setTitle("Location Permission Needed")
						.setMessage("This app needs the Location permission, please accept to use location functionality")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								ActivityCompat.requestPermissions(ItemDetailsActivity.this,
										new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
										MY_PERMISSIONS_REQUEST_LOCATION );
							}
						})
						.create()
						.show();


			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_LOCATION );
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					if (ContextCompat.checkSelfPermission(this,
							Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {

						mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
						map.setMyLocationEnabled(true);
					}

				} else {

					Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
				}
				return;
			}
		}
	}
}
