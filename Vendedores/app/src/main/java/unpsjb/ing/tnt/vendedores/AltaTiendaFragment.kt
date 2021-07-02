                                        package unpsjb.ing.tnt.vendedores

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AltaTiendaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AltaTiendaFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_alta_tienda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var ubicacion: String = ""
        var ubicacionLatLong: ArrayList<Double> = arrayListOf(0.0,0.0)
        val txt_direccion = view.findViewById<TextView>(R.id.txt_direccion)
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
                Log.i(TAG, "Place: ${place.name}, ${place.id}, ${place.address}, ${place.latLng}")
                ubicacion = place.name.toString()
                ubicacionLatLong = arrayListOf(place.latLng!!.latitude.toDouble(),place.latLng!!.longitude.toDouble())
                txt_direccion.setError(null)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }

        })




        val email = arguments?.getString("email")


        val rubros = resources.getStringArray(R.array.rubros)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, rubros)
        val atxt_rubro = view.findViewById<AutoCompleteTextView>(R.id.atxt_rubro)
        atxt_rubro.setAdapter(arrayAdapter)

        val btn_hora_inicio = view.findViewById<Button>(R.id.btn_hora_inicio)
        val btn_hora_cierre = view.findViewById<Button>(R.id.btn_hora_cierre)


        btn_hora_inicio.setOnClickListener{
            val timePicker = TimePickerFragment {
                btn_hora_inicio.setText("Hora de Inicio $it")
                btn_hora_inicio.setError(null)
                }
            timePicker.show(childFragmentManager, "timePicker")
        }

        btn_hora_cierre.setOnClickListener{
            val timePicker = TimePickerFragment {
                btn_hora_cierre.setText("Hora de Cierre $it")
                btn_hora_cierre.setError(null)
            }
            timePicker.show(childFragmentManager, "timePicker")
        }

        val db = FirebaseFirestore.getInstance()

        val btn_aceptar = view.findViewById<Button>(R.id.btn_aceptar)
        val txt_nombre = view.findViewById<EditText>(R.id.txt_nombre)
        val check_box_efectivo = view.findViewById<CheckBox>(R.id.check_box_efectivo)
        val check_box_debito = view.findViewById<CheckBox>(R.id.check_box_debito)
        val check_box_credito = view.findViewById<CheckBox>(R.id.check_box_credito)
        val txt_metodos_de_pago = view.findViewById<TextView>(R.id.txt_metodos_de_pago)


        check_box_efectivo.setOnClickListener(){
            if(check_box_efectivo.isChecked || check_box_debito.isChecked || check_box_credito.isChecked){
                txt_metodos_de_pago.setError(null)
            }else{
                txt_metodos_de_pago.error = "Debe seleccionar al menos un Metodo de Pago"
            }
        }

        check_box_debito.setOnClickListener(){
            if(check_box_efectivo.isChecked || check_box_debito.isChecked || check_box_credito.isChecked){
                txt_metodos_de_pago.setError(null)
            }else{
                txt_metodos_de_pago.error = "Debe seleccionar al menos un Metodo de Pago"
            }
        }

        check_box_credito.setOnClickListener(){
            if(check_box_efectivo.isChecked || check_box_debito.isChecked || check_box_credito.isChecked){
                txt_metodos_de_pago.setError(null)
            }else{
                txt_metodos_de_pago.error = "Debe seleccionar al menos un Metodo de Pago"
            }
        }

        atxt_rubro.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            atxt_rubro.setError(null)
        })
        btn_aceptar.setOnClickListener{
            if (txt_nombre.text.isEmpty() || atxt_rubro.text.isEmpty() || (ubicacion == "") ||
                    btn_hora_inicio.text.toString().length == 14 || btn_hora_cierre.text.toString().length == 14
                    || !(check_box_efectivo.isChecked || check_box_debito.isChecked || check_box_credito.isChecked)){
                if(txt_nombre.text.isEmpty()){
                    txt_nombre.error ="Debe completar el Nombre"
                }
                if(atxt_rubro.text.isEmpty()){
                    atxt_rubro.error = "Debe seleccionar un Rubro"
                }
                if(ubicacion == ""){
                    txt_direccion.error = "Debe introducir una Direccion"
                }
                if(btn_hora_inicio.text.toString().length == 14){
                    btn_hora_inicio.error = "Debe seleccionar un horario de Inicio"
                }
                if(btn_hora_cierre.text.toString().length == 14){
                    btn_hora_cierre.error = "Debe seleccionar un horario de Cierre"
                }
                if(!(check_box_efectivo.isChecked || check_box_debito.isChecked || check_box_credito.isChecked)){
                    txt_metodos_de_pago.error = "Debe seleccionar al menos un Metodo de Pago"
                }
            }else{
                val metodos_de_pago: ArrayList<String> = ArrayList()
                if(check_box_efectivo.isChecked){
                    metodos_de_pago.add(check_box_efectivo.text.toString())
                }
                if(check_box_debito.isChecked){
                    metodos_de_pago.add(check_box_debito.text.toString())
                }
                if(check_box_credito.isChecked){
                    metodos_de_pago.add(check_box_credito.text.toString())
                }
                db.collection("tiendas").document(txt_nombre.text.toString()).set(
                    hashMapOf(
                        "rubro" to atxt_rubro.text.toString(),
                        "ubicacion" to ubicacion,
                        "horario_de_atencion" to arrayListOf(
                            btn_hora_inicio.text.toString().substring(
                                15,
                                20
                            ), btn_hora_cierre.text.toString().substring(15, 20)
                        ),
                        "metodos_de_pago" to metodos_de_pago,
                        "usuario" to email.toString(),
                         "ubicacionLatLong" to ubicacionLatLong
                    )
                )
                Toast.makeText(
                    requireParentFragment().requireContext(),
                    "¡La tienda se ha creado con éxito!",
                    Toast.LENGTH_SHORT
                ).show()
                val bundle = bundleOf("email" to email.toString())
                findNavController().navigate(R.id.menuFragment, bundle)
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
         * @return A new instance of fragment AltaTiendaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AltaTiendaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}