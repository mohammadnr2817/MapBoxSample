package coding.mohammad.mapbox

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlin.math.acos
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private var mapView: MapView? = null

    private lateinit var linearAddMarker: LinearLayout
    private lateinit var linearAddPolygon: LinearLayout
    private lateinit var linearClearMap: LinearLayout

    private var counter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_main)

        findViews()
        mapView?.onCreate(savedInstanceState)

        checkPermission()

    }

    private fun findViews() {
        mapView = findViewById(R.id.mapView)
        linearAddMarker = findViewById(R.id.activity_main_linear_add_marker)
        linearAddPolygon = findViewById(R.id.activity_main_linear_add_polygon)
        linearClearMap = findViewById(R.id.activity_main_linear_clear_map)
    }

    private fun checkPermission() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            mapAsync()
        } else {
            Toast.makeText(this,"permission denied, enable it in app setting",Toast.LENGTH_SHORT).show()
            Log.d("mapBoxSample","permission denied")

            openAppInfo()
            finish()
        }
    }

    private fun openAppInfo() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    private fun mapAsync(){
        mapView?.getMapAsync { mapboxMap ->
            // Map is set up and the style has loaded
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {

                linearAddMarker!!.setOnClickListener {
                    addRandomMarker(mapboxMap)
                    Log.d("mapBoxSample","marker added!")
                }

                linearAddPolygon!!.setOnClickListener {
                    addRandomPolygon(mapboxMap)
                    Log.d("mapBoxSample","polygon added!")
                }

                linearClearMap!!.setOnClickListener {
                    mapboxMap.clear()
                    counter = 0
                    Log.d("mapBoxSample","map cleaned!")
                }

            }
        }
    }

    /**
     * Generate Random LatLng
     */
    private fun nextLatLng(): LatLng? {
        val u: Double = Random.nextDouble()
        val v: Double = Random.nextDouble()
        val latitude = Math.toDegrees(acos(u * 2 - 1)) - 90
        val longitude = 360 * v - 180
        return LatLng(latitude, longitude)
    }

    private fun addRandomMarker(mapboxMap: MapboxMap) {
        // create new random latlng
        val latLng = nextLatLng()
        // add that latlng to map as a new marker
        mapboxMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("marker $counter")
        )
        // animate camera position to that marker
        val position = CameraPosition.Builder()
            .target(latLng)
            .zoom(10.0)
            .tilt(20.0)
            .build()
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 3000)
        counter += 1
    }

    private fun addRandomPolygon(mapboxMap: MapboxMap) {
        // create new polygon with random points
        val rpoints = (1..5).random()
        val polygonLatLngList = ArrayList<LatLng>()
        for (i in 1 until rpoints){
            polygonLatLngList.add(nextLatLng()!!)
        }
        // add generated polygon to map
        mapboxMap.addPolygon(
            PolygonOptions()
                .addAll(polygonLatLngList)
                .fillColor(Color.parseColor("#6200EE"))
        )
    }

}