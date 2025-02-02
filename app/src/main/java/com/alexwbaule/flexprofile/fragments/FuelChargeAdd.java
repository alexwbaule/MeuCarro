package com.alexwbaule.flexprofile.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.view.ActionMode;
import android.text.Html;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.adapter.SpinnerCombustivelAdapter;
import com.alexwbaule.flexprofile.containers.Combustivel;
import com.alexwbaule.flexprofile.containers.DateTime;
import com.alexwbaule.flexprofile.containers.DetectedPlace;
import com.alexwbaule.flexprofile.domain.SQLiteHelper;
import com.alexwbaule.flexprofile.utils.PreferencesProcessed;
import com.alexwbaule.flexprofile.utils.SaveShowCalculos;
import com.alexwbaule.flexprofile.view.SeekbarWithIntervals;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by alex on 11/07/15.
 */
public class FuelChargeAdd extends BaseFragment implements NumberPickerDialogFragment.NumberPickerDialogHandler,
        TimePickerDialogFragment.TimePickerDialogHandler,
        CalendarDatePickerDialog.OnDateSetListener,
        OnMapReadyCallback {

    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private static final String ARG_PARAM1 = "HISTID";
    private static final String ARG_PARAM2 = "EDTID";
    private static final String ARG_PARAM3 = "DTID";

    private final String CLASSNAME = "FuelChargeAdd";
    private CalendarDatePickerDialog calendar;
    private long mParam1 = 0;

    private TextInputEditText fuel_add_date;
    private TextInputEditText fuel_add_time;
    private TextInputEditText fuel_add_km;
    private TextInputEditText fuel_add_lts;
    private TextInputEditText fuel_add_price;

    private TextInputLayout hint_fuel_km;
    private TextInputLayout hint_fuel_lts;
    private TextInputLayout hint_fuel_price;

    private SeekbarWithIntervals seekbarWithIntervals;
    private CheckBox fuel_add_partial;
    private LinearLayout fuel_add_proportion;
    private LinearLayout fuel_add_proportion_header;
    private LinearLayout location_search;

    OnChargeComplete mCallback;

    private Spinner fuel_add_comb;

    private boolean edited = false;

    private int EditUsed;
    private int EditDateUsed;
    private long[] ret;
    private double[] rettq;
    private PreferencesProcessed valores;
    private SaveShowCalculos saveshow;

    private ActionMode mActionMode;

    private SpinnerCombustivelAdapter spn1_data;

    private static final int REQUEST_PLACE_SEARCH = 1;

    private long place_id = 0;
    private TextView place_addr;
    private GoogleMap map;
    private MapView mapView;

    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private Place mPlace = null;
    private DetectedPlace detectedPlace;
    private FusedLocationProviderClient client;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = new ArrayList<>(Arrays.asList(Field.LAT_LNG, Field.NAME, Field.ADDRESS, Field.TYPES, Field.ID, Field.RATING, Field.PRICE_LEVEL));
    private List<DetectedPlace> detectedPlaces;
    private CameraPosition mCameraPosition;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getLong(ARG_PARAM1);
        }

        if (savedInstanceState != null) {
            EditUsed = savedInstanceState.getInt(ARG_PARAM2);
            EditDateUsed = savedInstanceState.getInt(ARG_PARAM3);
            edited = savedInstanceState.getBoolean("EDITED");
            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        detectedPlace = new DetectedPlace();
        detectedPlaces = new ArrayList<>();

        placesClient = Places.createClient(getActivity());
        client = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_PARAM2, EditUsed);
        outState.putInt(ARG_PARAM3, EditDateUsed);
        outState.putBoolean("EDITED", edited);
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnChargeComplete) getAppCompatActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getAppCompatActivity().toString() + " must implement OnDeleteChargeListener");
        }
    }


    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {
        Log.d(getClass().getSimpleName(), "********** onDateSet **************************");
        Calendar cal = new GregorianCalendar(i, i2, i3);
        String thisdate = android.text.format.DateFormat.getDateFormat(getActivity()).format(cal.getTime());
        TextInputEditText updatetxt = getActivity().findViewById(EditDateUsed);
        updatetxt.setText(thisdate);

        long[] ret;
        fuel_add_time = getActivity().findViewById(R.id.fuel_add_time);
        ret = rootapp.getRefuelIntevalKM(thisdate + " " + fuel_add_time.getText().toString(), String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
        hint_fuel_km = getActivity().findViewById(R.id.hint_fuel_km);
        hint_fuel_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_fuel_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        Log.d(CLASSNAME, "onMapReady called");

        try {
            if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES){
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style_night));
            }else {
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style_day));
            }
        } catch (Resources.NotFoundException e) {
            Log.e(CLASSNAME, "Can't find maps_style_day. Error: ", e);
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(CLASSNAME, "onMapClick called Lat=" + latLng.latitude + " Long=" + latLng.longitude);

            }
        });

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    Log.d(CLASSNAME, "The user gestured on the map.");
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
                    Log.d(CLASSNAME, "The user tapped something on the map.");
                } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
                    Log.d(CLASSNAME, "The app moved the camera.");
                }

            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null){
                    DetectedPlace place = (DetectedPlace) marker.getTag();
                    Log.d(CLASSNAME, "onMarkerClick called Lat=" + marker.getPosition().latitude + " Long=" + marker.getPosition().longitude);
                    place_addr.setText(Html.fromHtml("<b>" + place.getPlaceName() + "</b><br><i>" + place.getPlaceAddress() + "</i>"));
                    detectedPlace = place;
                }
                return false;
            }
        });

        MapsInitializer.initialize(this.getActivity());

        if (detectedPlace.isValid()) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            MapAndPlaceEdited(detectedPlace);
        } else {
            callPlaceDetectionApi();
        }
    }

    private void callPlaceDetectionApi() throws SecurityException {
        if (Build.VERSION.SDK_INT >= 23 && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            displayCurrentPosition();
            FindCurrentPlaceRequest currentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields);
            Task<FindCurrentPlaceResponse> currentPlaceTask = placesClient.findCurrentPlace(currentPlaceRequest);

            currentPlaceTask.addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                    double max = 0.0f;
                    List<PlaceLikelihood> likelyPlaces = findCurrentPlaceResponse.getPlaceLikelihoods();

                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        double isPlace = placeLikelihood.getLikelihood();
                        int retval = Double.compare(isPlace, max);
                        if (retval > 0) {
                            max = isPlace;
                            mPlace = placeLikelihood.getPlace();
                        }
                        detectedPlaces.add(new DetectedPlace(placeLikelihood.getPlace()));
                    }
                    detectedPlaces.remove(mPlace);
                    detectedPlaces.add(new DetectedPlace(mPlace, true));
                    if (detectedPlaces.size() > 0) {
                        MapAndPlace(detectedPlaces);
                    } else {
                        displayCurrentPosition();
                    }
                }
            });
        }
    }


    @SuppressLint("MissingPermission")
    protected void displayCurrentPosition() {
        client.getLastLocation().addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLastLocation = task.getResult();

                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    //Place current location marker
                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    //move map camera
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.animateCamera(CameraUpdateFactory.zoomTo(17));

                    Log.d(CLASSNAME, "OnCompleteListener called");

                }
            }
        });
    }

    protected void MapAndPlace(List<DetectedPlace> places) {
        map.clear();

        for (DetectedPlace place : places) {
            String description = "Local desconhecido";

            BitmapDescriptor icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_unknown);

            List<Place.Type> places_type = place.getPlaces_type();

            for (Place.Type type : places_type) {
                if (type == Place.Type.GAS_STATION) {
                    description = "Posto de combustível";
                    icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_gas);
                } else if (type == Place.Type.CAR_REPAIR) {
                    description = "Oficina";
                    icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_repair);
                } else if (type == Place.Type.CAR_WASH) {
                    description = "Lava-rápido";
                    icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_wash);
                }
            }

            mCurrLocationMarker = map.addMarker(new MarkerOptions()
                    .position(place.getCoord())
                    .title(place.getPlaceName())
                    .snippet(description)
                    .icon(icone));
            mCurrLocationMarker.setTag(place);

            if (place.getElectedPlace()) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getCoord(), 17);
                map.moveCamera(cameraUpdate);
                mCurrLocationMarker.setSnippet("Local detectado, selecione outro marcador para alterar.");
                mCurrLocationMarker.showInfoWindow();
                place_addr.setText(Html.fromHtml("<b>" + place.getPlaceName() + "</b><br><i>" + place.getPlaceAddress() + "</i>"));
                detectedPlace = place;
            }

        }
        Log.d(CLASSNAME, "MapAndPlace mPlace");
    }

    protected void MapAndPlaceEdited(DetectedPlace place) {
        if (place.isValid()) {
            map.clear();

            String description = "Local desconhecido";
            BitmapDescriptor icone;

            icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_unknown);

            List<Place.Type> places_type = place.getPlaces_type();

            for (Place.Type type : places_type) {
                if (type == Place.Type.GAS_STATION) {
                    description = "Posto de combustível";
                    icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_gas);
                } else if (type == Place.Type.CAR_REPAIR) {
                    description = "Oficina";
                    icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_repair);
                } else if (type == Place.Type.CAR_WASH) {
                    description = "Lava-rápido";
                    icone = BitmapDescriptorFactory.fromResource(R.drawable.ico_maps_wash);
                }
            }
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getCoord(), 17);
            map.moveCamera(cameraUpdate);
            mCurrLocationMarker = map.addMarker(new MarkerOptions()
                    .position(place.getCoord())
                    .title(place.getPlaceName())
                    .snippet(description)
                    .icon(icone));
            mCurrLocationMarker.showInfoWindow();
            place_addr.setText(Html.fromHtml("<b>" + place.getPlaceName() + "</b><br><i>" + place.getPlaceAddress() + "</i>"));
        }
        Log.d(CLASSNAME, "MapAndPlaceEdited mPlace => " + place.toString());
    }

    public interface OnChargeComplete {
        void onCharge();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        saveshow = new SaveShowCalculos(rootapp);
        NumberFormat parser = NumberFormat.getInstance();
        parser.setMaximumFractionDigits(3);
        //parser.setMinimumFractionDigits(2);

        mActionMode = getAppCompatActivity().startSupportActionMode(mActionModeCallback);
        mActionMode.setTitle(rootapp.getString(R.string.novo_abastecimento));

        valores = new PreferencesProcessed(getActivity());
        DateTime today = rootapp.splitedNowDateTime(Calendar.getInstance().getTime());

        ret = rootapp.getRefuelIntevalKM(today.getDate() + " " + today.getTime(), String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
        rettq = rootapp.getMaxFuelCapacity();

        ArrayList<Combustivel> data = rootapp.getCombustiveisFromCar();
        spn1_data = new SpinnerCombustivelAdapter(getActivity(), R.layout.spinner_combustivel_add, data);

        View rootView = inflater.inflate(R.layout.fragment_fuelcharge_add, container, false);

        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(null);
        //mapView.setClickable(false);
        mapView.getMapAsync(this);

        location_search = rootView.findViewById(R.id.locationSearch);
        location_search.setOnClickListener(searchplace);

        place_addr = rootView.findViewById(R.id.place_addr);

        hint_fuel_km = rootView.findViewById(R.id.hint_fuel_km);
        hint_fuel_lts = rootView.findViewById(R.id.hint_fuel_lts);
        hint_fuel_price = rootView.findViewById(R.id.hint_fuel_price);


        fuel_add_date = rootView.findViewById(R.id.fuel_add_date);
        fuel_add_time = rootView.findViewById(R.id.fuel_add_time);
        fuel_add_km = rootView.findViewById(R.id.fuel_add_km);
        fuel_add_lts = rootView.findViewById(R.id.fuel_add_lts);
        fuel_add_price = rootView.findViewById(R.id.fuel_add_price);
        seekbarWithIntervals = rootView.findViewById(R.id.seekbarWithIntervals);
        ArrayList<String> values = new ArrayList<>();
        values.add(getString(R.string.vazio_sign));
        values.add(getString(R.string.v14_sign));
        values.add(getString(R.string.v12_sign));
        values.add(getString(R.string.v34_sign));
        values.add(getString(R.string.cheio_sign));
        seekbarWithIntervals.setIntervals(values);

        fuel_add_proportion = rootView.findViewById(R.id.fuel_add_proportion);
        fuel_add_proportion_header = rootView.findViewById(R.id.fuel_add_proportion_header);

        fuel_add_partial = rootView.findViewById(R.id.fuel_add_partial);

        fuel_add_partial.setOnCheckedChangeListener(checkbox_checked);
        fuel_add_proportion_header.setVisibility(View.GONE);
        fuel_add_proportion.setVisibility(View.GONE);

        fuel_add_date.setOnClickListener(opencalendar);
        fuel_add_date.setText(today.getDate());


        fuel_add_time.setOnClickListener(meuclicktime);
        fuel_add_time.setText(today.getTime());

        if (ret[0] == 0) {
            hint_fuel_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        } else {
            hint_fuel_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }
        hint_fuel_price.setHint(getString(R.string.price_by_liter, valores.getVolumeTanque(), getString(R.string.simbolo_moeda)));

        hint_fuel_price.setError(getString(R.string.total_price, getString(R.string.simbolo_moeda), "-"));

        fuel_add_price.setTag(getString(R.string.simbolo_moeda));
        fuel_add_price.setOnClickListener(meuclick);

        fuel_add_km.setTag(getString(R.string.km_atual, valores.getOdometro()));
        fuel_add_km.setOnClickListener(meuclick);

        hint_fuel_lts.setHint(valores.getVolumeTanque());
        hint_fuel_lts.setError(getString(R.string.volume_capacity, saveshow.getShowVolumeTanque(rettq[0])));
        fuel_add_lts.setTag(valores.getVolumeTanque());

        fuel_add_lts.setOnClickListener(meuclick);

        fuel_add_comb = rootView.findViewById(R.id.fuel_add_comb);
        fuel_add_comb.setAdapter(spn1_data);
        fuel_add_comb.setOnItemSelectedListener(spinSelect);

        if (mParam1 > 0) {
            Cursor cursor = rootapp.getOneFuel(mParam1);
            mActionMode.setTitle(rootapp.getString(R.string.editar_abastecimento));

            cursor.moveToFirst();
            String thisdate = cursor.getString(cursor.getColumnIndex(SQLiteHelper.HIST_DATE));
            String thistime = cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_TIME));
            boolean ispartial = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.HIST_PARTIAL)) == 1;
            fuel_add_date.setText(thisdate);
            fuel_add_time.setText(thistime);
            fuel_add_km.setText(saveshow.getShowOdometrov2(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_KM)), false));
            fuel_add_lts.setText(saveshow.getShowVolumeMix(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_LTS)), cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_COMB_ID))));
            fuel_add_price.setText(parser.format(cursor.getDouble(cursor.getColumnIndex(SQLiteHelper.HIST_PRICE))));
            fuel_add_comb.setSelection(spn1_data.getPosition(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.KEY_COMB_ID))), true);
            if (ispartial) {
                fuel_add_partial.setChecked(ispartial);
                seekbarWithIntervals.setProgress(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.HIST_PROPORTION)));
            }
            place_id = cursor.getLong(cursor.getColumnIndex(SQLiteHelper.PLACE_ID));
            detectedPlace = rootapp.getPlaceMark(String.valueOf(place_id));
            cursor.close();
            ret = rootapp.getRefuelIntevalKM(thisdate + " " + thistime, String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
            hint_fuel_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
            if (ret[1] == 0) {
                hint_fuel_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
            }
            edited = true;
            calcTotalPrice(rootView);
        }
        return rootView;
    }

    private CompoundButton.OnCheckedChangeListener checkbox_checked = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                fuel_add_proportion_header.setVisibility(View.VISIBLE);
                fuel_add_proportion.setVisibility(View.VISIBLE);
            } else {
                fuel_add_proportion_header.setVisibility(View.GONE);
                fuel_add_proportion.setVisibility(View.GONE);
            }
        }
    };

    private AdapterView.OnItemSelectedListener spinSelect = new AdapterView.OnItemSelectedListener() {

        @SuppressLint("StringFormatMatches")
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            NumberFormat parser = NumberFormat.getInstance();
            parser.setMaximumFractionDigits(3);
            //parser.setMinimumFractionDigits(2);

            Combustivel remv = (Combustivel) parent.getSelectedItem();

            hint_fuel_lts = getActivity().findViewById(R.id.hint_fuel_lts);
            hint_fuel_price = getActivity().findViewById(R.id.hint_fuel_price);
            fuel_add_price = getActivity().findViewById(R.id.fuel_add_price);


            if (remv.getCombustivel().equals("GNV")) {
                hint_fuel_lts.setHint(valores.getVolumeGNV());
                hint_fuel_lts.setError(getString(R.string.volume_capacity, saveshow.getShowVolumeGNV(rettq[1]), valores.getVolumeSimbolGNV()));
                hint_fuel_price.setHint(getString(R.string.price_by_liter, valores.getVolumeGNV(), getString(R.string.simbolo_moeda)));
            } else {
                hint_fuel_lts.setHint(valores.getVolumeTanque());
                hint_fuel_lts.setError(getString(R.string.volume_capacity, saveshow.getShowVolumeTanque(rettq[0])));
                hint_fuel_price.setHint(getString(R.string.price_by_liter, valores.getVolumeTanque(), getString(R.string.simbolo_moeda)));
            }
            if (!edited && remv.getId() > 0) {
                double[] prices = rootapp.getPrices();
                fuel_add_price.setText(parser.format(prices[remv.getId() - 1]));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener meuclicktime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditUsed = v.getId();
            TimePickerBuilder dpb = new TimePickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setTargetFragment(FuelChargeAdd.this)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            dpb.show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_SEARCH && resultCode == Activity.RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            detectedPlaces.remove(place);
            detectedPlaces.add(new DetectedPlace(place, true));
            MapAndPlace(detectedPlaces);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    View.OnClickListener searchplace = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
            RectangularBounds bounds = RectangularBounds.newInstance(visibleRegion.latLngBounds);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields)
                    .setLocationRestriction(bounds)
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_PLACE_SEARCH);
        }
    };

    View.OnClickListener meuclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditUsed = v.getId();
            NumberPickerBuilder dpb = new NumberPickerBuilder()
                    .setFragmentManager(getFragmentManager())
                    .setLabelText(v.getTag().toString())
                    .setTargetFragment(FuelChargeAdd.this)
                    .setPlusMinusVisibility(View.GONE)
                    .setStyleResId(R.style.BetterPickersDialogFragment_Light);
            if (v.getId() == R.id.fuel_add_km) {
                dpb.setDecimalVisibility(View.GONE);
            }
            if (v.getId() == R.id.fuel_add_price)
                edited = true;
            dpb.show();
        }
    };

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        boolean isfuture = false;
        DateTime today = rootapp.splitedNowDateTime(Calendar.getInstance().getTime());
        fuel_add_date = getActivity().findViewById(R.id.fuel_add_date);

        TextInputEditText updatetxt = getActivity().findViewById(EditUsed);
        String thistime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        String thisdate = fuel_add_date.getText().toString();

        String[] hora = thistime.split(":");
        String[] hora_2 = today.getTime().split(":");

        if (today.getDate().equals(thisdate)) {
            if ((Integer.valueOf(hora[0]) == Integer.valueOf(hora_2[0])) && (Integer.valueOf(hora[1]) > Integer.valueOf(hora_2[1]))) {
                isfuture = true;
            } else if (Integer.valueOf(hora[0]) > Integer.valueOf(hora_2[0])) {
                isfuture = true;
            }
        }

        if (isfuture) {
            MeuCarroApplication.WarnUser(getView(), "Não é possível selecionar um horário no futuro", Snackbar.LENGTH_LONG, true, null);
            thistime = today.getTime();
        }

        updatetxt.setText(thistime);

        long[] ret;
        ret = rootapp.getRefuelIntevalKM(thisdate + " " + thistime, String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));
        hint_fuel_km = getActivity().findViewById(R.id.hint_fuel_km);
        hint_fuel_km.setError(getString(R.string.intervalo_valido, String.valueOf(ret[0]), String.valueOf(ret[1]), saveshow.getOdometro()));
        if (ret[1] == 0) {
            hint_fuel_km.setError(getString(R.string.km_hint_anterior, String.valueOf(ret[0]), saveshow.getOdometro()));
        }
    }

    protected void calcTotalPrice(View v) {
        NumberFormat format2 = new DecimalFormat("0.00");
        NumberFormat parseFloat = NumberFormat.getInstance();
        Number ParsedPrice = 0;
        Number ParsedLts = 0;

        fuel_add_lts = v.findViewById(R.id.fuel_add_lts);
        fuel_add_price = v.findViewById(R.id.fuel_add_price);
        hint_fuel_price = v.findViewById(R.id.hint_fuel_price);

        if ((fuel_add_lts != null) && (fuel_add_price != null)) {
            if (fuel_add_lts.getText().length() > 0 && fuel_add_price.getText().length() > 0) {
                try {
                    ParsedPrice = parseFloat.parse(fuel_add_price.getText().toString());
                    ParsedLts = parseFloat.parse(fuel_add_lts.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                hint_fuel_price.setError(getString(R.string.total_price, getString(R.string.simbolo_moeda), format2.format(ParsedPrice.doubleValue() * ParsedLts.doubleValue())));
            }
        }
    }

    @Override
    public void onDialogNumberSet(int i, int i2, double v, boolean b, double v2) {
        NumberFormat format = new DecimalFormat();
        TextInputEditText updatetxt = getActivity().findViewById(EditUsed);
        updatetxt.setText(format.format(v2));
        calcTotalPrice(getView());
    }

    private View.OnClickListener opencalendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditDateUsed = v.getId();
            Calendar teste = parseDate(((TextInputEditText) v).getText().toString());
            calendar = CalendarDatePickerDialog.newInstance(FuelChargeAdd.this, teste.get(Calendar.YEAR), teste.get(Calendar.MONTH), teste.get(Calendar.DAY_OF_MONTH));
            calendar.setYearRange(Calendar.getInstance().get(Calendar.YEAR) - 10, Calendar.getInstance().get(Calendar.YEAR));
            calendar.setMaxDate(Calendar.getInstance());
            calendar.setTargetFragment(FuelChargeAdd.this, 0);
            calendar.show(getFragmentManager(), "Calendar");
        }
    };

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the user exits the action mode
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.save, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            replaceFragment(new FuelCharge());
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_submenu_cancel) {
                mode.finish();
            } else {
                item.setEnabled(false);
                fuel_add_date = getActivity().findViewById(R.id.fuel_add_date);
                fuel_add_time = getActivity().findViewById(R.id.fuel_add_time);
                fuel_add_km = getActivity().findViewById(R.id.fuel_add_km);
                fuel_add_lts = getActivity().findViewById(R.id.fuel_add_lts);
                fuel_add_comb = getActivity().findViewById(R.id.fuel_add_comb);
                fuel_add_partial = getActivity().findViewById(R.id.fuel_add_partial);
                seekbarWithIntervals = getActivity().findViewById(R.id.seekbarWithIntervals);

                String fuel_date_time = fuel_add_date.getText().toString() + " " + fuel_add_time.getText().toString();


                boolean isPartial = fuel_add_partial.isChecked();
                int proportion = seekbarWithIntervals.getProgress();

                NumberFormat parser = NumberFormat.getInstance();
                parser.setMaximumFractionDigits(3);
                //parser.setMinimumFractionDigits(2);


                long new_km = 0;
                double new_lts = 0;
                double new_price = 0;
                long place_id = 0;
                rettq = rootapp.getMaxFuelCapacity();

                Combustivel comb = (Combustivel) fuel_add_comb.getSelectedItem();
                DecimalFormat format = new DecimalFormat();

                try {
                    new_km = format.parse(fuel_add_km.getText().toString()).longValue();
                } catch (NumberFormatException | ParseException e) {
                    new_km = 0;
                }

                try {
                    new_lts = format.parse(fuel_add_lts.getText().toString()).doubleValue();
                } catch (NumberFormatException | ParseException e) {
                    new_lts = 0;
                }

                try {
                    new_price = format.parse(fuel_add_price.getText().toString()).doubleValue();
                } catch (NumberFormatException | ParseException e) {
                    new_price = 0;
                }

                ret = rootapp.getRefuelIntevalKM(fuel_date_time, String.valueOf(rootapp.getDefaultCar()), String.valueOf(mParam1));

                if (new_lts == 0) {
                    Log.d(getClass().getSimpleName(), "NEW LTS is " + new_lts);
                    MeuCarroApplication.WarnUser(getView(), getString(R.string.o_volume_nao_pode, parser.format(new_lts)), Snackbar.LENGTH_LONG, true, item);
                    return true;
                } else {
                    if (comb.getId() == 3) {
                        if (new_lts > rettq[1]) {
                            MeuCarroApplication.WarnUser(getView(), getString(R.string.o_volume_nao_pode_max, parser.format(rettq[1]), parser.format(new_lts)), Snackbar.LENGTH_LONG, true, item);
                            return true;
                        }
                    } else {
                        if (new_lts > rettq[0]) {
                            MeuCarroApplication.WarnUser(getView(), getString(R.string.o_volume_nao_pode_max, parser.format(rettq[0]), parser.format(new_lts)), Snackbar.LENGTH_LONG, true, item);
                            return true;
                        }
                    }
                }

                if (comb.getId() == 0) {
                    MeuCarroApplication.WarnUser(getView(), R.string.selecione_um_combustivel, Snackbar.LENGTH_LONG, true, item);
                    return true;
                }
                if (ret[1] > 0 && ret[1] <= new_km) {
                    MeuCarroApplication.WarnUser(getView(), getString(R.string.km_atual_maior, new_km, ret[0]), Snackbar.LENGTH_LONG, true, item);
                    return true;
                }

                if (ret[0] >= new_km) {
                    MeuCarroApplication.WarnUser(getView(), getString(R.string.km_atual_menor, new_km, ret[0]), Snackbar.LENGTH_LONG, true, item);
                    return true;
                }

                Log.d(CLASSNAME, "mPlace --> " + detectedPlace.getPlaceAddress());

                if (detectedPlace.isValid())
                    place_id = rootapp.createPlace(detectedPlace);

                if (mParam1 > 0) {
                    if (!rootapp.updateCharge(mParam1, new_lts, new_km, comb.getId(), fuel_date_time, new_price, isPartial, proportion, place_id))
                        return false;
                } else {
                    if (!rootapp.includeCharge(new_lts, new_km, comb.getId(), fuel_date_time, new_price, isPartial, proportion, place_id))
                        return false;
                }
                mCallback.onCharge();
                mode.finish();
            }
            item.setEnabled(true);
            return true;
        }
    };
}

