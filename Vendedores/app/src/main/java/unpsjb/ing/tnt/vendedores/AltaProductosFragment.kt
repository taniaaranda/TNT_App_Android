package unpsjb.ing.tnt.vendedores

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import unpsjb.ing.tnt.vendedores.databinding.FragmentAltaProductosBinding

class AltaProductosFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentAltaProductosBinding
    private lateinit var altaProductosView: View
    private lateinit var tienda: String
    private lateinit var usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_alta_productos, container, false
        )

        altaProductosView = binding.root
        usuario = arguments?.getString("usuario").toString()
        tienda = arguments?.getString("tienda").toString()

        return altaProductosView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categorias = resources.getStringArray(R.array.categoria)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categorias)
        val atxt_categoria = view.findViewById<AutoCompleteTextView>(R.id.atxt_categoria)
        atxt_categoria.setAdapter(arrayAdapter)

        atxt_categoria.setOnItemClickListener (AdapterView.OnItemClickListener{ parent, view, position, id ->
            atxt_categoria.setError(null)
        })

        binding.btnAceptar.setOnClickListener {
            crearProducto()
        }
    }

    private fun crearProducto() {
        if (formularioValido()) {
            val metodos_de_pago: ArrayList<String> = ArrayList()
            if (binding.productoPrecio.text.isEmpty()) {
                metodos_de_pago.add(binding.productoPrecio.text.toString())
            }

            val tienda = getDbReference().collection("tiendas").document(tienda).get()
            tienda
                .addOnSuccessListener {
                    getDbReference().collection("productos").document(binding.productoNombre.text.toString()).set(
                        hashMapOf(
                            "cantidadDisponible" to binding.cantidadDisponible.text.toString(),
                            "precioUnitario" to binding.productoPrecio.text.toString(),
                            "tienda" to tienda
                        )
                    )

                    Toast.makeText(requireParentFragment().requireContext(), "¡Producto cargado con éxito!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.menuFragment)
                }
                .addOnFailureListener {
                }
        }
    }

    private fun formularioValido(): Boolean {
        if (binding.productoNombre.text.isEmpty() || binding.cantidadDisponible.text.isEmpty() ||
                (binding.productoPrecio.text.isEmpty())) {
            if (binding.productoNombre.text.isEmpty()) {
                binding.productoNombre.error ="Debe completar el Nombre"
            }

            if (binding.productoPrecio.text.isEmpty()) {
                binding.productoPrecio.error ="Debe completar el Precio"
            }

            if (binding.cantidadDisponible.text.isEmpty()) {
                binding.cantidadDisponible.error = "Debe completar la Cantidad Disponible"
            }

            return false
        }

        return true
    }
}