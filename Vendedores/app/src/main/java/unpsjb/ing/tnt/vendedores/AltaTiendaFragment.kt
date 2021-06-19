package unpsjb.ing.tnt.vendedores

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        val rubros = resources.getStringArray(R.array.rubros)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, rubros)
        val atxt_rubro = view.findViewById<AutoCompleteTextView>(R.id.atxt_rubro)
        atxt_rubro.setAdapter(arrayAdapter)

        val btn_hora_inicio = view.findViewById<Button>(R.id.btn_hora_inicio)

        btn_hora_inicio.setOnClickListener{
            val timePicker = TimePickerFragment { btn_hora_inicio.setText("Hora de Inicio $it") }
            timePicker.show(childFragmentManager, "timePicker")
        }

        val btn_hora_cierre = view.findViewById<Button>(R.id.btn_hora_cierre)

        btn_hora_cierre.setOnClickListener{
            val timePicker = TimePickerFragment { btn_hora_cierre.setText("Hora de Cierre $it") }
            timePicker.show(childFragmentManager, "timePicker")
        }
        val db = FirebaseFirestore.getInstance()

        val btn_aceptar = view.findViewById<Button>(R.id.btn_aceptar)
        val txt_nombre = view.findViewById<EditText>(R.id.txt_nombre)
        val txt_ubicacion = view.findViewById<EditText>(R.id.txt_ubicacion)
        btn_aceptar.setOnClickListener{
            if (txt_nombre.text.isNotEmpty()){
                db.collection("tiendas").document(txt_nombre.text.toString()).set(
                        hashMapOf("rubro" to atxt_rubro.text.toString(),
                                "ubicacion" to txt_ubicacion.text.toString()


                        )
                )
            }else{
                txt_nombre.error ="Debe completar el nombre"
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