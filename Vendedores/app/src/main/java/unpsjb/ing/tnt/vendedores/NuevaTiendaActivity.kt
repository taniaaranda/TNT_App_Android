package unpsjb.ing.tnt.vendedores

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NuevaTiendaActivity : AppCompatActivity() {
    private var db: FirebaseFirestore = Firebase.firestore
    private var _user: FirebaseUser? = null
    private val currentUser get() = _user

    private lateinit var responseView: TextView

    var rubros: HashMap<String, String> = HashMap()
    var ubicacion: String = ""
    var ubicacionLatLong: HashMap<String, Double> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_tienda)

        checkLogIn()
        loadData()
        setFormListeners()
        setButtonsActions()
    }

    private fun checkLogIn() {
        _user = FirebaseAuth.getInstance().currentUser;
        if (currentUser == null) {
            Log.d("HomeActivity", "User is not logged in, redirecting to login...")
            finish()
            startActivity(Intent(this@NuevaTiendaActivity, UnauthorizedActivity::class.java))
        }
    }

    private fun loadData() {
        db.collection("rubros").get()  // TODO Transformar en fragment
            .addOnSuccessListener {
                rubros.clear()
                //val nombresRubros = ArrayList<String>()
                rubros.put("-", "Elija un rubro")
                val textView = findViewById<TextView>(R.id.textView)
                textView.text = "Seleccione un Rubro:"
                //nombresRubros.add("Elija un rubro")
                val nombresRubros = arrayOf("Despensa", "Ferreteria", "Mercería","Heladería", "Rotiseria")

                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    this,
                    R.layout.support_simple_spinner_dropdown_item, nombresRubros
                )

                val view = findViewById<Spinner>(R.id.rubros_list)
                view.adapter = adapter
            }
            .addOnFailureListener {
                Log.i("NuevaTiendaActivity", "No hay rubros para cargar");
            }

        val horarios = ArrayList<String>()
        horarios.add("Elegir horario")
        for (hora in 7..21) {
            for (minuto in 0..59 step 15) {
                horarios.add(hora.toString().padStart(2, '0') + ":" + minuto.toString().padStart(2, '0'))
            }
        }
        val horarioAperturaAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item, horarios
        )

        val horarioAperturaView = findViewById<Spinner>(R.id.horarios_apertura_list)  // TODO Transformar en fragment
        horarioAperturaView.adapter = horarioAperturaAdapter

        val horarioCierreAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item, horarios
        )

        val horarioCierreView = findViewById<Spinner>(R.id.horarios_cierre_list)  // TODO Transformar en fragment
        horarioCierreView.adapter = horarioCierreAdapter
    }

    private fun setFormListeners() {
        Places.initialize(
            applicationContext,
            getString(R.string.android_sdk_places_apt_key)
        )

        responseView = findViewById<TextView>(R.id.autocomplete_response_content)
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS))

        // Listen to place selection events
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}, ${place.address}, ${place.latLng}")
                ubicacion = place.name.toString()
                ubicacionLatLong["lat"] = place.latLng!!.latitude
                ubicacionLatLong["lng"] = place.latLng!!.longitude
                responseView.error = null
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }

        })
    }

    private fun setButtonsActions() {
        findViewById<Button>(R.id.btn_aceptar).setOnClickListener {
            if (formValido()) {
                Log.d("NuevaTiendaActivity", "Aceptar!")
                val payload = getTiendaPayload()
                Log.d("NuevaTiendaActivity", payload.toString())
                db.collection("tiendas").add(getTiendaPayload())
                    .addOnSuccessListener {
                        Log.d("NuevaTiendaActivity", "Tienda creada con éxito")
                        Toast.makeText(applicationContext, "¡La tienda se ha creado con éxito!", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this@NuevaTiendaActivity, HomeActivity::class.java))
                    }
                    .addOnFailureListener {
                        Log.d("NuevaTiendaActivity", "La tienda no fue creada")
                        Toast.makeText(applicationContext, "¡La tienda no fue creada!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(applicationContext, "Revise los datos ingresados por favor", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btn_cancelar).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            startActivity(Intent(this, UnauthorizedActivity::class.java))
        }
    }

    private fun formValido(): Boolean {
        var valido = true

        val nombre = findViewById<EditText>(R.id.nombre).text.toString()
        if (nombre.isEmpty()) {
            valido = false
        }

        val rubroView = findViewById<Spinner>(R.id.rubros_list)
        if (rubroView.selectedItem.toString() == "Elija un rubro") {
            valido = false
        }

        if (ubicacion.isEmpty()) {
            valido = false
        }

        if (ubicacionLatLong.isEmpty()) {
            valido = false
        }

        val horarioApertura = findViewById<Spinner>(R.id.horarios_apertura_list).selectedItem.toString()
        if (horarioApertura == "Elija un horario") {
            valido = false
        }

        val horarioCierre = findViewById<Spinner>(R.id.horarios_cierre_list).selectedItem.toString()
        if (horarioCierre == "Elija un horario") {
            valido = false
        }

        if (getMetodosDePago().isEmpty()) {
            valido = false
        }

        return valido
    }

    private fun getTiendaPayload(): HashMap<String, Any> {
        return hashMapOf(
            "nombre" to getNombre(),
            "rubro" to getRubro(),
            "calle" to getCalle(),
            "horario_de_atencion" to getHorariosDeAtencion(),
            "metodos_de_pago" to getMetodosDePago(),
            "usuario" to getUserUuid(),
            "ubicacionLatLong" to getUbicacionLatLong()
        )
    }

    private fun getNombre(): String {
        return findViewById<EditText>(R.id.nombre).text.toString()
    }

    private fun getUbicacionLatLong(): Any {
        return ubicacionLatLong
    }

    private fun getUserUuid(): String {
        return currentUser?.uid.toString()
    }

    private fun getMetodosDePago(): ArrayList<String> {
        val metodosDePago: ArrayList<String> = ArrayList()

        val efectivoView = findViewById<CheckBox>(R.id.check_box_efectivo)
        if (efectivoView.isChecked) {
            metodosDePago.add(efectivoView.text.toString())
        }

        val debitoView = findViewById<CheckBox>(R.id.check_box_debito)
        if (debitoView.isChecked) {
            metodosDePago.add(debitoView.text.toString())
        }

        val creditoView = findViewById<CheckBox>(R.id.check_box_credito)
        if (creditoView.isChecked) {
            metodosDePago.add(creditoView.text.toString())
        }

        return metodosDePago
    }

    private fun getHorariosDeAtencion(): HashMap<String, String> {
        val horarios = hashMapOf<String, String>()

        horarios["apertura"] = findViewById<Spinner>(R.id.horarios_apertura_list).selectedItem.toString()
        horarios["cierre"] = findViewById<Spinner>(R.id.horarios_cierre_list).selectedItem.toString()

        return horarios
    }

    private fun getCalle(): Any {
        return ubicacion
    }

    private fun getRubro(): String {
        val nombreRubro = findViewById<Spinner>(R.id.rubros_list).selectedItem.toString()
        return nombreRubro

    }
}