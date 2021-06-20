package unpsjb.ing.tnt.vendedores

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
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
        val txt_ubicacion = view.findViewById<EditText>(R.id.txt_ubicacion)
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

        atxt_rubro.setOnItemClickListener (AdapterView.OnItemClickListener{ parent, view, position, id ->
            atxt_rubro.setError(null)
        })
        btn_aceptar.setOnClickListener{
            if (txt_nombre.text.isEmpty() || atxt_rubro.text.isEmpty() || txt_ubicacion.text.isEmpty() ||
                    btn_hora_inicio.text.toString().length == 14 || btn_hora_cierre.text.toString().length == 14
                    || !(check_box_efectivo.isChecked || check_box_debito.isChecked || check_box_credito.isChecked)){
                if(txt_nombre.text.isEmpty()){
                    txt_nombre.error ="Debe completar el Nombre"
                }
                if(atxt_rubro.text.isEmpty()){
                    atxt_rubro.error = "Debe seleccionar un Rubro"
                }
                if(txt_ubicacion.text.isEmpty()){
                    txt_ubicacion.error = "Debe completar la Ubicación"
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
                        hashMapOf("rubro" to atxt_rubro.text.toString(),
                                "ubicacion" to txt_ubicacion.text.toString(),
                                "horario_de_atencion" to arrayListOf(btn_hora_inicio.text.toString().substring(15,20),btn_hora_cierre.text.toString().substring(15,20)),
                                "metodos_de_pago" to metodos_de_pago
                        )
                )
                AlertDialog.Builder(context).apply{
                    setTitle("¡La tienda se ha creado con éxito!")
                    setPositiveButton("Aceptar"){_: DialogInterface, _: Int ->
                        findNavController().navigate(R.id.menuFragment)
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