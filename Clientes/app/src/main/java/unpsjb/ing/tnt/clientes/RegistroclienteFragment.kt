package unpsjb.ing.tnt.clientes

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistroclienteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistroclienteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registrocliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre = view.findViewById<EditText>(R.id.nombre)
        val email = view.findViewById<EditText>(R.id.email)
        val pass = view.findViewById<EditText>(R.id.pass)
        val txt_direccion = view.findViewById<TextView>(R.id.txt_direccion)
        val button_registrocliente = view.findViewById<Button>(R.id.button_registrocliente)

        var direccion: String = ""
        var ubicacionLatLong: ArrayList<Double> = arrayListOf(0.0,0.0)
        Places.initialize(
            requireContext().applicationContext,
            getString(R.string.android_sdk_places_apt_key)
        )
        //val placesClient: PlacesClient = Places.createClient()
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}, ${place.address}, ${place.latLng}")
                direccion = place.name.toString()
                ubicacionLatLong = arrayListOf(place.latLng!!.latitude.toDouble(),place.latLng!!.longitude.toDouble())
                txt_direccion.setError(null)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }

        })

        val db = FirebaseFirestore.getInstance()


        button_registrocliente.setOnClickListener {
            if (email.text.isNotEmpty() && pass.text.isNotEmpty() && (direccion!="") && (nombre.text.isNotEmpty())) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            db.collection("datosClientes").add(hashMapOf(
                                    "usuario" to email.text.toString(),
                                    "nombre" to nombre.text.toString(),
                                    "direccion" to direccion,
                                    "ubicacionLatLong" to ubicacionLatLong

                            ))

                            Toast.makeText(
                                requireParentFragment().requireContext(),
                                "¡El usuario se ha creado con éxito!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val bundle = bundleOf("email" to email.text.toString())
                            findNavController().navigate(R.id.homeFragment, bundle)
                        } else {
                            AlertDialog.Builder(context).apply{
                                setTitle("¡Debe ingresar un email con forma xxx@xxxx y una contraseña alfanumerica!")
                                setPositiveButton("Aceptar"){ _: DialogInterface, _: Int ->
                                    findNavController().navigate(R.id.registroclienteFragment)
                                }
                            }.show()
                        }
                        }

            } else {
                AlertDialog.Builder(context).apply{
                    setTitle("¡Debe ingresar los datos requeridos!")
                    setPositiveButton("Aceptar"){ _: DialogInterface, _: Int ->
                        findNavController().navigate(R.id.registroclienteFragment)
                    }
                }.show()
            }

        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistroclienteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistroclienteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}