package unpsjb.ing.tnt.clientes.ui.auth

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.UnauthorizedActivity
import unpsjb.ing.tnt.clientes.ui.utils.FirebaseConnectedFragment

class RegistroFragment : FirebaseConnectedFragment() {
    private lateinit var nombreView: EditText
    private lateinit var emailView: EditText
    private lateinit var claveView: EditText
    private lateinit var direccionView: TextView
    private lateinit var ubicacionView: AutocompleteSupportFragment
    private lateinit var botonRegistro: Button

    private lateinit var ubicacion: ArrayList<Double>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadViews(view)
        setUbicacionListener()
        setRegistroListener()
    }

    private fun setRegistroListener() {
        botonRegistro.setOnClickListener {
            if (datosValidos()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailView.text.toString(), claveView.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            ejecutarAccionesPostRegistro()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("Registration", it.message.toString())

                        if (it::class == FirebaseAuthUserCollisionException::class) {
                            Toast.makeText(context, "El email ya esta en uso", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No se pudo guardar la registracion, por favor reintente", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Verifique los datos ingresados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ejecutarAccionesPostRegistro() {
        FirebaseAuth.getInstance().currentUser!!
            .updateProfile(userProfileChangeRequest {
                displayName = nombreView.text.toString()
            })
            .addOnSuccessListener {
                guardarDireccion()
            }
    }

    private fun guardarDireccion() {
        getDbReference().collection("direcciones")
            .add(hashMapOf(
                "usuario" to FirebaseAuth.getInstance().currentUser!!.uid,
                "ubicacion" to getUbicacion(),
                "calle" to direccionView.text.toString()
            ))
            .addOnSuccessListener {
                (activity as UnauthorizedActivity)
                    .logIn(emailView.text.toString(), claveView.text.toString())
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ocurrió un error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUbicacion(): HashMap<String, Double> {
        return hashMapOf(
            "lat" to ubicacion[0],
            "lng" to ubicacion[1]
        )
    }

    private fun datosValidos(): Boolean {
        if (
            emailView.text.isNotEmpty() &&
            nombreView.text.isNotEmpty() &&
            claveView.text.isNotEmpty() &&
            direccionView.text.isNotEmpty() &&
            ubicacion.isNotEmpty()
        ) {
            return true
        }

        return false
    }

    private fun loadViews(view: View) {
        nombreView = view.findViewById(R.id.nombre)
        emailView = view.findViewById(R.id.email)
        claveView = view.findViewById(R.id.clave)
        direccionView = view.findViewById(R.id.direccion)
        botonRegistro = view.findViewById(R.id.boton_registro)

        ubicacion = arrayListOf(0.0, 0.0)
        Places.initialize(
            requireContext().applicationContext,
            getString(R.string.android_sdk_places_apt_key)
        )
        ubicacionView = childFragmentManager.findFragmentById(R.id.ubicacion) as AutocompleteSupportFragment
        ubicacionView.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
    }

    private fun setUbicacionListener() {
        ubicacionView.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(
                    ContentValues.TAG,
                    "Place: ${place.name}, ${place.id}, ${place.address}, ${place.latLng}"
                )
                direccionView.text = place.name.toString()
                ubicacion = arrayListOf(place.latLng!!.latitude, place.latLng!!.longitude)
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })
    }
}