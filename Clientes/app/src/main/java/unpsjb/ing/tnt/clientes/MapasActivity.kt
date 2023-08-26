package unpsjb.ing.tnt.clientes
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.databinding.ActivityHomeBinding

class MapasActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    private var _user: FirebaseUser? = null
    private val currentUser get() = _user

    private lateinit var map: GoogleMap
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }
    private fun createMarker() {
        val favoritePlace = LatLng(-43.257271318886424, -65.30789083036679)
        map.addMarker(MarkerOptions().position(favoritePlace).title("El DIT!"))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
            4000,
            null
        )

        FirebaseFirestore.getInstance().collection("tiendas").get()
            .addOnSuccessListener {
                val tiendas: ArrayList<Tienda> = arrayListOf()
                for (document in it.documents) {
                    tiendas.add(Tienda(
                        document .id,
                        document .get("nombre").toString(),
                        document .get("rubro").toString(),
                        document .get("calle").toString(),
                        document .get("ubicacionLatLong") as HashMap<String, Double>,
                        document .get("horario_de_atencion") as HashMap<String, String>,
                        document .get("metodos_de_pago") as ArrayList<String>
                    ))

                    for (value in tiendas) {
                        val lng = value.ubicacion["lng"] as Double
                        val lat = value.ubicacion["lat"] as Double
                        val ubicacion = LatLng(lat, lng)
                        val markerOptions = MarkerOptions().position(ubicacion).title(value.nombre).snippet(value.rubro + " - " + value.horarioDeAtencion)
                        map.addMarker(markerOptions)
                        Log.d("MARTIN", value.ubicacion["lng"].toString())
                        Log.d("MARTIN", value.ubicacion["lat"].toString())
                    }
                }
            }
            .addOnFailureListener {
                // Aca haces algo con el fallo, sea cual sea, mostrar un Toast de error, hacer un Log.e() o lo que sea
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapas)
        createMapFragment()
    }
    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}