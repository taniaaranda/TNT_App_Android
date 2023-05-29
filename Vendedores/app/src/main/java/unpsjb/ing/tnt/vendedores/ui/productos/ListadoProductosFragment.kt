package unpsjb.ing.tnt.vendedores.ui.productos

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.adapter.ProductoAdapter
import unpsjb.ing.tnt.vendedores.data.model.Producto
import unpsjb.ing.tnt.vendedores.databinding.FragmentProductosBinding
import java.util.ArrayList

private const val PRODUCTOS_COLLECTION_NAME = "productos"

class ListadoProductosFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentProductosBinding
    private lateinit var listView: View
    private lateinit var fragmentContext: Context
    private lateinit var tienda: String
    private var productosSnapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTienda()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_productos, container, false
        )

        fragmentContext = this.requireContext()
        listView = binding.root

        return listView
    }

    private fun loadTienda() {
        getDbReference().collection("tiendas")
            .whereEqualTo("usuario", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                tienda = it.documents.first().id
                Log.d("ListadoProductosFmnt", "Tienda: ${it.documents.first().id}")

                registerButtonAction()

                binding.nombreFiltro.doOnTextChanged { text, start, before, count ->
                    registerProductoSnapshotListener(text.toString())
                }

                registerProductoSnapshotListener()
            }
            .addOnFailureListener {
                Toast.makeText(context, "No se pudo obtener la tienda", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registerButtonAction() {
        val bundle = bundleOf("tienda" to tienda)

        val btn_alta_producto = listView.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.btn_alta_producto
        )
        btn_alta_producto.setOnClickListener{
            findNavController().navigate(R.id.product_new, bundle)
        }
    }

    private fun registerProductoSnapshotListener(filtro: String = "") {
        productosSnapshotListener?.remove()

        productosSnapshotListener = getDbReference().collection(PRODUCTOS_COLLECTION_NAME)
            .whereEqualTo("tienda", tienda)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ListadoProductos", e.message.toString())
                    return@addSnapshotListener
                }

                val adapter =
                    ProductoAdapter(this.requireContext(), parseProductos(snapshots, filtro))
                binding.listadoProductos.adapter = adapter
            }
    }

    private fun hydrateProducto(document: DocumentSnapshot): Producto {
        return Producto(
            document.id,
            document.get("nombre") as String,
            document.get("observaciones") as String,
            document.get("precio").toString().toLong(),
            document.get("stock").toString().toLong(),
            document.get("foto") as String,
            document.get("categoria") as String,
            document.get("exceso_azucar") as Boolean,
            document.get("exceso_sodio") as Boolean,
            document.get("exceso_grasas_sat") as Boolean,
            document.get("exceso_grasas_tot") as Boolean,
            document.get("exceso_calorias") as Boolean,
            document.get("tienda") as String
        )
    }

    private fun parseProductos(snapshots: QuerySnapshot?, filtro: String): List<Producto> {
        val productos = ArrayList<Producto>()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                if (!Producto.validateDocument(document)) {
                    continue
                }

                var producto: Producto? = null

                if (filtro != "") {
                    // TODO: No es performante para nada esto
                    if (document.get("nombre").toString().contains(filtro, ignoreCase = true)) {
                        producto = hydrateProducto(document)
                    }
                } else {
                    producto = hydrateProducto(document)
                }

                if (producto != null) {
                    productos.add(producto)
                }
            }
        }

        return productos.sortedWith(compareBy { it.id })
    }
}