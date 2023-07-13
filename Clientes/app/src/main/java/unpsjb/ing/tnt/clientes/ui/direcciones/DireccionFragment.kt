package unpsjb.ing.tnt.clientes.ui.direcciones

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Direccion
import unpsjb.ing.tnt.clientes.databinding.FragmentDireccionBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class DireccionFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentDireccionBinding
    private lateinit var direccionView: View
    private lateinit var ubicacionView: AutocompleteSupportFragment

    private var modo: String = "Ver"
    private var direccion: Direccion? = null
    private lateinit var ubicacion: ArrayList<Double>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_direccion, container, false
        )
        direccionView = binding.root

        setData()
        setView()
        setUbicacionListener()
        setBotonGuardar()

        return direccionView
    }

    private fun setData() {
        modo = requireArguments().getString("modo").toString()
        val direccionUsuario = requireArguments().getString("direccion")

        if (direccionUsuario != null) {
            direccion = ClientesApplication.usuario!!.direcciones.find {
                it.id == direccionUsuario
            }
        }
    }

    private fun setView() {
        ubicacion = arrayListOf(0.0, 0.0)
        Places.initialize(
            requireContext().applicationContext,
            getString(R.string.android_sdk_places_apt_key)
        )
        ubicacionView = childFragmentManager.findFragmentById(R.id.ubicacion) as AutocompleteSupportFragment
        ubicacionView.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))

        if (modo == "nuevo") {
            setCrearView()
        } else if (modo == "editar") {
            setEditarView()
        } else {
            setVerView()
        }
    }

    private fun setVerView() {
        binding.guardarDireccion.visibility = View.GONE
        binding.direccionTitulo.text = "Dirección"
    }

    private fun setEditarView() {
        binding.guardarDireccion.visibility = View.VISIBLE
        binding.direccionTitulo.text = "Editar Dirección"
        binding.direccion.text = direccion!!.calle
    }

    private fun setCrearView() {
        binding.guardarDireccion.visibility = View.VISIBLE
        binding.direccionTitulo.text = "Nueva Dirección"
    }

    private fun setUbicacionListener() {
        ubicacionView.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(
                    ContentValues.TAG,
                    "Place: ${place.name}, ${place.id}, ${place.address}, ${place.latLng}"
                )

                val direccionElegida = place.name.toString()
                val ubicacionElegida = arrayListOf(place.latLng!!.latitude, place.latLng!!.longitude)
                binding.direccion.text = ""

                if (direccion == null) {
                    direccion = Direccion(
                        "",
                        direccionElegida,
                        hashMapOf("lat" to ubicacionElegida[0], "lng" to ubicacionElegida[1]),
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                } else {
                    direccion!!.calle = direccionElegida
                    direccion!!.ubicacion = hashMapOf("lat" to ubicacionElegida[0], "lng" to ubicacionElegida[1])
                }
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })
    }

    private fun setBotonGuardar() {
        binding.guardarDireccion.setOnClickListener {
            if (direccion == null) {
                Toast.makeText(context, "¡Tenes que elegir una dirección!", Toast.LENGTH_SHORT).show()
            } else {
                direccion!!.guardar()
                    .addOnSuccessListener {
                        Toast.makeText(context, "¡Guardado!", Toast.LENGTH_SHORT).show()
                        direccionView.findNavController().navigate(R.id.nav_addresses)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "¡No se pudo guardar!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}