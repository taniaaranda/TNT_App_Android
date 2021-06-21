package unpsjb.ing.tnt.vendedores

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AltaProductosFragment : FirebaseConnectedFragment() {
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
        return inflater.inflate(R.layout.fragment_alta_productos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categorias = resources.getStringArray(R.array.categoria)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categorias)
        val atxt_categoria = view.findViewById<AutoCompleteTextView>(R.id.atxt_categoria)
        atxt_categoria.setAdapter(arrayAdapter)

        val db = FirebaseFirestore.getInstance()

        val btn_aceptar = view.findViewById<Button>(R.id.btn_aceptar)
        val productoNombre = view.findViewById<EditText>(R.id.productoNombre)
        val cantidadDisponible = view.findViewById<EditText>(R.id.cantidadDisponible)
        val productoPrecio = view.findViewById<EditText>(R.id.productoPrecio)
        val DescripcionProductos = view.findViewById<TextView>(R.id.DescripcionProducto)

        atxt_categoria.setOnItemClickListener (AdapterView.OnItemClickListener{ parent, view, position, id ->
            atxt_categoria.setError(null)
        })

        btn_aceptar.setOnClickListener{
            if (productoNombre.text.isEmpty() || cantidadDisponible.text.isEmpty() ||
                (productoPrecio.text.isEmpty())){
                if(productoNombre.text.isEmpty()){
                    productoNombre.error ="Debe completar el Nombre"
                }
                if(productoPrecio.text.isEmpty()){
                    productoPrecio.error ="Debe completar el Precio"
                }
                if(cantidadDisponible.text.isEmpty()){
                    cantidadDisponible.error = "Debe completar la Cantidad Disponible"
                }

            }else{
                val metodos_de_pago: ArrayList<String> = ArrayList()
                if(productoPrecio.text.isEmpty()){
                    metodos_de_pago.add(productoPrecio.text.toString())
                }

                db.collection("productos").document(productoNombre.text.toString()).set(
                    hashMapOf("cantidadDisponible" to cantidadDisponible.text.toString(),
                        "precioUnitario" to productoPrecio.text.toString()
                    )
                )
                AlertDialog.Builder(context).apply{
                            setTitle("¡El Producto se ha creado con éxito!")
                            setPositiveButton("Aceptar"){ _: DialogInterface, _: Int ->
                                findNavController().navigate(R.id.menuFragment)
                            }
                }.show()
            }
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            AltaProductosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}