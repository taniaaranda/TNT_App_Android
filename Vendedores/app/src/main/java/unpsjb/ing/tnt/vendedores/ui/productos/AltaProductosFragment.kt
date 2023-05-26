package unpsjb.ing.tnt.vendedores.ui.productos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.databinding.FragmentAltaProductosBinding

class AltaProductosFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentAltaProductosBinding
    private lateinit var altaProductosView: View
    private lateinit var tiendaId: String
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
        tiendaId = arguments?.getString("tienda").toString()

        return altaProductosView
    }

    private fun prepareSpinner(categorias: ArrayList<String>) {
        val categoriasAdapter = ArrayAdapter(this.requireContext(),
            android.R.layout.simple_spinner_item, categorias)
        binding.categorias.setAdapter(categoriasAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tienda = getDbReference().collection("tiendas").document(tiendaId).get()
        tienda.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                /** TODO: Quitar en algun momento y reemplazar por las categorias cargadas para esa tienda */
                val rubro = documentSnapshot.get("rubro").toString()
                prepareSpinner(arrayListOf(rubro))
                /** TODO: Fin todo*/

                binding.btnAceptar.setOnClickListener {
                    crearProducto(documentSnapshot)
                }
            } else {
                // TODO: Mostrar mensaje de tienda no existente o mandarlo a la creacion de tienda.
            }
        }
        .addOnFailureListener {
            Log.d("AltaProductos", "Tienda no ok")
        }
    }

    private fun crearProducto(tienda: DocumentSnapshot) {
        if (formularioValido()) {
            getDbReference().collection("productos")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { resultProductos ->
                    var id = "1"

                    if (resultProductos.documents.isNotEmpty()) {
                        val lastId = (resultProductos.documents.first().get("id") as String).toInt()
                        id = (lastId + 1).toString()
                    }

                    persistirProducto(id, tienda)
                }
                .addOnFailureListener {
                    // TODO: Mostrar un mensaje acorde
                }
        }
    }

    private fun persistirProducto(id: String, tienda: DocumentSnapshot) {
        val metodos_de_pago: ArrayList<String> = ArrayList()
        if (binding.productoPrecio.text.isEmpty()) {
            metodos_de_pago.add(binding.productoPrecio.text.toString())
        }

        var categoria = ""
        if (binding.categorias.isNotEmpty()) {
            categoria = binding.categorias.selectedItem.toString()
        }

        getDbReference().collection("productos")
            .document().set(
                hashMapOf(
                    "id" to id,
                    "nombre" to binding.productoNombre.text.toString(),
                    "cantidadDisponible" to binding.cantidadDisponible.text.toString().toLong(),
                    "precioUnitario" to binding.productoPrecio.text.toString().toLong(),
                    "categoria" to categoria,
                    "fotografia" to "",
                    "observaciones" to binding.DescripcionProducto.text.toString(),
                    "tienda" to tiendaId
                )
            )
            .addOnSuccessListener {
                Toast.makeText(
                    requireParentFragment().requireContext(),
                    "¡Producto cargado con éxito!",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(R.id.home, bundleOf("email" to tienda.get("usuario")))
            }
            .addOnFailureListener {
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

            // TODO: Validar que haya seleccionado una categoría también

            return false
        }

        return true
    }
}