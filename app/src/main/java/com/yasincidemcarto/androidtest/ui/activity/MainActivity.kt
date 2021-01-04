package com.yasincidemcarto.androidtest.ui.activity

import android.Manifest
import android.content.*
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.yasincidemcarto.androidtest.R
import com.yasincidemcarto.androidtest.core.Constants.DEFAULT_ANIM_DELAY_SLOW
import com.yasincidemcarto.androidtest.core.Constants.FAKE_LOCATION
import com.yasincidemcarto.androidtest.core.Constants.FAKE_LOCATION_TITLE
import com.yasincidemcarto.androidtest.core.Constants.FAKE_POI
import com.yasincidemcarto.androidtest.core.ext.*
import com.yasincidemcarto.androidtest.core.util.EspressoIdlingResource
import com.yasincidemcarto.androidtest.core.util.ResultOf
import com.yasincidemcarto.androidtest.databinding.ActivityMainBinding
import com.yasincidemcarto.androidtest.datasource.model.Poi
import com.yasincidemcarto.androidtest.ui.MainViewModel
import com.yasincidemcarto.androidtest.ui.fragment.PoiListDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMyLocationButtonClickListener {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap
    private lateinit var fakeLocationMarker: MarkerOptions
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var poiDetailBottomSheet: BottomSheetBehavior<View>
    private lateinit var poiRouteBottomSheet: BottomSheetBehavior<View>
    private lateinit var poiChooseStartPointBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var poiNavigationBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private var polyLine: Polyline? = null
    private val markersMap = mutableMapOf<String, Marker>()
    private var isRegisteredReceiver = false
    private val bbox = LatLngBounds.Builder()
    private val gpsStateChanged = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setMarkerVisibility(true)
            if (shouldGPSUpdated()) {
                val isEnabled = viewModel.setIsFakeLocationEnabled(isFakeLocationEnabled())
                if (isEnabled) showSnackBar(getString(R.string.gps_enabled)) else showSnackBar(getString(
                    R.string.gps_disabled
                ))
            }
        }
    }
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setMarkerVisibility(true)
                registerReceiver()
                viewModel.setIsFakeLocationEnabled(isFakeLocationEnabled())
            } else
                setMarkerVisibility(false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        locationManager = (this.getSystemService(LOCATION_SERVICE)) as LocationManager
        WindowCompat.setDecorFitsSystemWindows(window, true)
        initializeModels()
        initializeMap()
        initializeViews()
    }

    private fun initializeModels() {
        viewModel.apply {
            getInitialPois()
            setIsFakeLocationEnabled(isFakeLocationEnabled())
        }
    }

    private fun initializeMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        EspressoIdlingResource.increment()
        mapFragment.getMapAsync(this@MainActivity)
    }

    private fun initializeViews() {
        binding.searchPoiView.setOnSafeClickListener {
            PoiListDialogFragment().show(supportFragmentManager, "poi_list_bottom_sheet")
        }

        binding.myLocation.setOnSafeClickListener {
            if (viewModel.isFakeLocationEnabled.value == true)
                map.animateCamera(CameraUpdateFactory.newLatLng(FAKE_LOCATION), 500, null)
            else
                requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.poiDetails.directions.setOnSafeClickListener {
            hidePoiDetail()
            if (viewModel.isFakeLocationEnabled.value == true) {
                viewModel.setStartPoint(FAKE_POI)
                showPoiRoute()
            } else
                showChooseStartPointBottomSheet()
        }

        binding.poiRoute.navigate.setOnSafeClickListener {
            hidePoiRoute()
            animateRouteOnMap()
            showNavigation()
        }

        binding.poiNavigation.finishNavigating.setOnSafeClickListener {
            hideNavigation()
            animateAllMarkers()
            viewModel.setIsFakeLocationEnabled(isFakeLocationEnabled())
            clearOnBackPressed()
        }

        poiDetailBottomSheet = BottomSheetBehavior.from(binding.poiDetails.root).apply(::hideBottomSheet)
        poiRouteBottomSheet = BottomSheetBehavior.from(binding.poiRoute.root).apply(::hideBottomSheet)
        poiChooseStartPointBottomSheet = BottomSheetBehavior.from(binding.chooseStartPoint.root).apply(::hideBottomSheet)
        poiNavigationBottomSheet = BottomSheetBehavior.from(binding.poiNavigation.root).apply(::hideBottomSheet)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        EspressoIdlingResource.decrement()
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.setOnMyLocationButtonClickListener(this)
        map.uiSettings.isCompassEnabled = false

        if (isFakeLocationEnabled())
            map.animateCamera(CameraUpdateFactory.newLatLng(FAKE_LOCATION), 500, null)
        else
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        fakeLocationMarker = MarkerOptions()
            .title(FAKE_LOCATION_TITLE)
            .position(FAKE_LOCATION)
            .anchor(0.5f, 0.5f)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bluedot))

        markersMap[FAKE_LOCATION_TITLE] = map.addMarker(fakeLocationMarker)

        viewModel.apply {
            listOfPoi.observe(this@MainActivity) { response ->
                handlePoiList(response)
            }
            isFakeLocationEnabled.observe(this@MainActivity) { isEnabled ->
                drawFakeLocationIcon(isEnabled)
            }
            zipNonNullableLiveData(startPoint, endPoint).observe(this@MainActivity) { (start, end) ->
                calculateRouteAndUpdateUI(start, end)
            }
            zipFirstNullableLiveData(previousPoint, selectedPoint).observe(this@MainActivity) { (previousPoi, selectedPoi) ->
                handleStartEndStates(previousPoi, selectedPoi)
            }
        }
    }

    private fun handlePoiList(response: ResultOf<List<Poi>>) {
        when (response) {
            is ResultOf.Success -> {
                addMarkers()
            }
            is ResultOf.Failure -> {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleStartEndStates(previousPoi: Poi?, selectedPoi: Poi?) {
        if (selectedPoi != null) {
            //region Handle new poi selection
            if (previousPoi != null)
                markersMap[previousPoi.id]?.setIcon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED
                    )
                )
            clickMarker(markersMap[selectedPoi.id]!!)
            map.animateCamera(
                CameraUpdateFactory.newLatLng(
                    selectedPoi.getAsCoordinate()
                ), 500, null
            )
            if (shouldShowPoiDetails())
                showPoiDetail()
            //endregion

            //region Handle start and end points
            viewModel.apply {
                if (isFakeLocationEnabled.value == true) {
                    setEndPoint(selectedPoi)
                } else if (shouldContinueWithPoiToPoiFlow()) {
                    if (endPoint.value == null)
                        setEndPoint(selectedPoi)
                    setStartPoint(selectedPoi)
                    clickMarker(markersMap[endPoint.value?.id]!!)
                    hideChooseStartPoint()
                    showPoiRoute()
                }
            }
            //endregion
        }
    }

    private fun addMarkers() {
        val pois = viewModel.listOfPoi.value
        if (pois is ResultOf.Success) {
            pois.data.map {
                val latLng = LatLng(it.latitude, it.longitude)
                bbox.include(latLng)
                MarkerOptions()
                    .title(it.id)
                    .position(latLng)
            }.forEach {
                markersMap[it.title] = map.addMarker(it)
            }
            if (pois.data.isNotEmpty())
                animateAllMarkers()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val pois = viewModel.listOfPoi.value
        if (shouldMarkerNonClickable())
            return true
        if (marker.title != FAKE_LOCATION_TITLE && pois is ResultOf.Success)
            viewModel.updatePoint(pois.data.first { it.id == marker.title })
        return true
    }

    private fun calculateRouteAndUpdateUI(start: Poi?, end: Poi?) {
        if (start != null && end != null) {
            drawRoute(
                viewModel.startPoint.value?.getAsCoordinate(),
                viewModel.endPoint.value?.getAsCoordinate()
            )
            val result = viewModel.calculateDistance(start.getAsCoordinate(), end.getAsCoordinate())
            val time = result.time()
            val distance = result.metersToKm().apply { getString(R.string.distance, this) }
            binding.poiRoute.time.text = time
            binding.poiRoute.distance.text = distance
        }
    }

    private fun drawRoute(start: LatLng?, end: LatLng?) {
        if (this::map.isInitialized) {
            removeRoute()
            polyLine = map.addPolyline(
                PolylineOptions()
                    .add(start)
                    .add(end)
                    .color(ContextCompat.getColor(this, R.color.purple_500))
                    .width(5.toPx().toFloat())
            )
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(FAKE_LOCATION, 10f))
        return true
    }

    override fun onResume() {
        super.onResume()
        if (isLocationPermissionGranted()) {
            setMarkerVisibility(true)
            if (isRegisteredReceiver.not())
                registerReceiver()
        } else
            setMarkerVisibility(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegisteredReceiver) unregisterReceiver(gpsStateChanged)
    }

    override fun onBackPressed() {
        when {
            poiNavigationBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN -> {
                hideNavigation()
                animateAllMarkers()
                viewModel.setIsFakeLocationEnabled(isFakeLocationEnabled())
                clearOnBackPressed()
            }
            poiRouteBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN -> {
                hidePoiRoute()
                clearOnBackPressed()
            }
            poiChooseStartPointBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN -> {
                hideChooseStartPoint()
                clearOnBackPressed()
            }
            poiDetailBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN -> {
                hidePoiDetail()
                clearOnBackPressed()
            }
            poiDetailBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN -> super.onBackPressed()
        }
    }

    private fun clearOnBackPressed() {
        resetMarkers()
        clearPointSelection()
        removeRoute()
    }

    private fun clearPointSelection() {
        viewModel.setStartPoint(null)
        viewModel.setEndPoint(null)
        viewModel.selectedPoint.value = null
    }

    private fun isFakeLocationEnabled(): Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isLocationPermissionGranted()

    private fun registerReceiver() {
        if (isRegisteredReceiver.not()) {
            registerReceiver(
                gpsStateChanged,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
            isRegisteredReceiver = true
        }
    }

    private fun animateAllMarkers() {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bbox.build(), 50.toPx()))
    }

    private fun animateRouteOnMap() {
        val currentPlace: CameraPosition = CameraPosition.Builder()
            .target(viewModel.startPoint.value?.getAsCoordinate())
            .bearing(viewModel.startPoint.value?.getAsCoordinate()?.toLocation()!!
                .bearingTo(
                    viewModel.endPoint.value?.getAsCoordinate()!!.toLocation()
                )
            )
            .tilt(66f)
            .zoom(18f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
    }

    private fun drawFakeLocationIcon(isEnabled: Boolean) =
        if (isEnabled)
            markersMap[FAKE_LOCATION_TITLE]?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bluedot))
        else
            markersMap[FAKE_LOCATION_TITLE]?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_greydot))

    private fun clickMarker(marker: Marker) = marker.setIcon(
        BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_GREEN
        )
    )

    private fun resetMarkers() {
        markersMap[viewModel.previousPoint.value?.id]?.setIcon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )
        markersMap[viewModel.startPoint.value?.id]?.setIcon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )
        markersMap[viewModel.endPoint.value?.id]?.setIcon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )
        markersMap[viewModel.selectedPoint.value?.id]?.setIcon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )
    }

    private fun setMarkerVisibility(isVisible: Boolean) {
        markersMap[FAKE_LOCATION_TITLE]?.isVisible = isVisible
    }

    private fun removeRoute() = polyLine?.remove()
    private fun hidePoiDetail() = hideBottomSheet(poiDetailBottomSheet)
    private fun showPoiDetail() = expandBottomSheet(poiDetailBottomSheet, true)
    private fun hidePoiRoute() = hideBottomSheet(poiRouteBottomSheet)
    private fun showPoiRoute() = expandBottomSheet(poiRouteBottomSheet, true)
    private fun hideChooseStartPoint() = hideBottomSheet(poiChooseStartPointBottomSheet)
    private fun showChooseStartPointBottomSheet() = expandBottomSheet(poiChooseStartPointBottomSheet, true, DEFAULT_ANIM_DELAY_SLOW)
    private fun hideNavigation() {
        binding.searchPoiView.isClickable = true
        applySlideAnimation(binding.appBarLayout, View.VISIBLE)
        hideBottomSheet(poiNavigationBottomSheet)
    }
    private fun showNavigation() {
        binding.searchPoiView.isClickable = false
        applySlideAnimation(binding.appBarLayout, View.GONE)
        expandBottomSheet(poiNavigationBottomSheet, true)
    }
    private fun showSnackBar(text: String) = Snackbar.make(
        binding.root,
        text,
        Snackbar.LENGTH_SHORT
    )
        .setDuration(1000)
        .show()
    private fun shouldGPSUpdated() =
        poiChooseStartPointBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN &&
                poiNavigationBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN &&
                poiRouteBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN &&
                poiDetailBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN
    private fun shouldMarkerNonClickable() =
        poiNavigationBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED ||
                poiNavigationBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN
    private fun shouldContinueWithPoiToPoiFlow() =
        poiChooseStartPointBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN ||
                poiRouteBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN
    private fun shouldShowPoiDetails() =
        poiRouteBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN &&
                poiChooseStartPointBottomSheet.state == BottomSheetBehavior.STATE_HIDDEN &&
                viewModel.endPoint.value == null
}