package com.group15.todoList;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.AdapterView.OnItemSelectedListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.group15.todoList.model.DataItem;
import com.group15.todoList.model.DataItem.ItemTypes;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ItemDetailsActivity extends AppCompatActivity implements OnClickListener, OnMapReadyCallback {

	protected static final String logger = ItemDetailsActivity.class
			.getSimpleName();

	public static final String ARG_ITEM_OBJECT = "itemObject";

	public static final int RESPONSE_ITEM_UPDATED = 1;

	public static final int RESPONSE_ITEM_DELETED = 2;

	public static final int RESPONSE_NOCHANGE = -1;

	private GoogleMap map;
	LocationRequest mLocationRequest;
	Location mLastLocation;
	Marker mCurrLocationMarker;
	FusedLocationProviderClient mFusedLocationClient;

	private DataItem item;

	private EditText itemName;
	private EditText itemDescription;
	private CheckBox itemIsDone;
	private CalendarView itemDate;
	private LatLng itemCoords;

	private Button saveButton;
	private Button deleteButton;

//	protected Spinner iconpathSpinner;

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
			this.itemIsDone = (CheckBox) findViewById(R.id.item_favourite);
			this.itemDate = (CalendarView) findViewById(R.id.item_date);

			this.saveButton = (Button) findViewById(R.id.saveButton);
			this.deleteButton = (Button) findViewById(R.id.deleteButton);
//			this.iconpathSpinner = (Spinner) findViewById(R.id.item_iconname);


			this.item = (DataItem) getIntent().getSerializableExtra(
					ARG_ITEM_OBJECT);

			// Brandenburg THB coords - 52.411509, 12.539004
//			this.itemCoords = new LatLng(52.411509, 12.539004);

			if (this.item != null) {
				setTitle("Todo Details");

				itemName.setText(item.getName());
				itemDescription.setText(item.getDescription());
				itemIsDone.setChecked(item.isDone());
				itemDate.setDate(item.getDate());
			} else {
				setTitle("New Todo");
				deleteButton.setVisibility(View.GONE);

				this.item = new DataItem(-1, ItemTypes.TYPE1, "", "",
						false, itemDate.getMinDate());
			}

			this.saveButton.setOnClickListener(this);
			this.deleteButton.setOnClickListener(this);

//			iconpathSpinner
//					.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//						@Override
//						public void onItemSelected(AdapterView<?> arg0,
//								View arg1, int arg2, long arg3) {
//							Log.d(logger, "got a selection: " + arg2);
//							String selectedItem = (String) arg0
//									.getSelectedItem();
//
//							String imgpath = getIconId4Iconname(selectedItem);
//
//							updateBackgroundImage(imgpath);
//						}
//
//						@Override
//						public void onNothingSelected(AdapterView<?> arg0) {
//							// TODO Auto-generated method stub
//
//						}
//					});

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
		// re-set the fields of the item

		if (!this.validateFields()) {
			return;
		}

		this.item.setName(this.itemName.getText().toString());
		this.item.setDescription(this.itemDescription.getText().toString());
		this.item.setIsDone(this.itemIsDone.isChecked());
		this.item.setDate(this.itemDate.getDate());

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
			Log.i(logger, "got onClick() on saveButton");
			processItemSave();
		} else if (view == this.deleteButton) {
			Log.i(logger, "got onClick() ond deleteButton");
			processItemDelete();
		} else {
			Log.w(logger,
					"got onClick() on view where it will not be handled: "
							+ view);
		}
	}

	protected int getIconIdIndex(String iconId) {
		return Arrays.asList(
				getResources()
						.getStringArray(R.array.background_resources_list))
				.indexOf(iconId);
	}

	protected String getIconId4Iconname(String name) {
		return getResources().getStringArray(R.array.background_resources_list)[Arrays
				.asList(getResources().getStringArray(R.array.background_list))
				.indexOf(name)];
	}

	private void updateBackgroundImage(final String resourcepath) {

		Log.d(logger, "updating background image using resource: " + resourcepath);

		new AsyncTask<Void, Void, Object>() {

			@Override
			protected Object doInBackground(Void... arg) {
				try {
					final Drawable imgcontent = new BitmapDrawable(getResources(),
							BitmapFactory
									.decodeStream(((DataAccessRemoteApplication) getApplication())
											.getMediaResourceAccessor()
											.readMediaResource(resourcepath + ".png")));
					Log.d(logger, "got content: " + imgcontent + " of class " + imgcontent != null ? imgcontent.getClass().getName() : "<null>");

					return imgcontent;
				} catch (Exception e) {
					return e;
				}
			}

			@Override
			protected void onPostExecute(Object result) {
				if (result instanceof Drawable) {
					((ViewGroup) findViewById(R.id.saveButton).getParent())
							.setBackground((Drawable) result);
				} else {
					((DataAccessRemoteApplication) getApplication()).reportError(
							ItemDetailsActivity.this,
							((Exception) result).getMessage());
					((Exception)result).printStackTrace();
				}
			}

		}.execute();
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


	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(@NonNull LatLng coordinates) {
				Log.i(logger, "coords" + coordinates);
				itemCoords = coordinates;

				if (mCurrLocationMarker != null) {
					mCurrLocationMarker.setPosition(coordinates);
				} else {
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(coordinates);
					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

					mCurrLocationMarker = map.addMarker(markerOptions);
				}

				map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 14));
			}
		});

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(new LatLng(52.411509, 12.539004));
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

		mCurrLocationMarker = map.addMarker(markerOptions);

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.411509, 12.539004), 14));

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
