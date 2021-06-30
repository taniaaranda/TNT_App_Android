package unpsjb.ing.tnt.vendedores

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.vendedores.adapter.ProductoAdapter
import unpsjb.ing.tnt.vendedores.data.model.Producto
import unpsjb.ing.tnt.vendedores.databinding.FragmentItemListBinding
import java.util.*

private const val PRODUCTOS_COLLECTION_NAME = "productos"

class ListadoProductosFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentItemListBinding
    private lateinit var listView: View
    private lateinit var fragmentContext: Context
    private var productosSnapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_item_list, container, false
        )

        fragmentContext = this.requireContext()
        listView = binding.root
        binding.nombreFiltro.doOnTextChanged { text, start, before, count ->
            registerProductoSnapshotListener(text.toString())
        }

        registerProductoSnapshotListener()

        return listView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tienda = arguments?.getString("tienda")
        val bundle = bundleOf("tienda" to tienda.toString())

        val btn_alta_producto = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btn_alta_producto)
        btn_alta_producto.setOnClickListener{
            findNavController().navigate(R.id.altaProductosFragment, bundle)
        }
    }

    private fun registerProductoSnapshotListener(filtro: String = "") {
        productosSnapshotListener?.remove()

        productosSnapshotListener = getDbReference().collection(PRODUCTOS_COLLECTION_NAME)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ListadoProductos", e.message.toString())
                    return@addSnapshotListener
                }

                val adapter = ProductoAdapter(this.requireContext(), parseProductos(snapshots, filtro))
                binding.listadoProductos.adapter = adapter
            }
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
                    if (document.get("nombre").toString().contains(filtro, ignoreCase = true)) {
                        producto = Producto(
                            document.get("id") as String,
                            document.get("nombre") as String,
                            document.get("cantidadDisponible") as Long,
                            document.get("precioUnitario") as Long,
                            document.get("categoria") as String,
                            document.get("fotografia") as String,
                            document.get("observaciones") as String
                        )
                    }
                } else {
                    producto = Producto(
                        document.get("id") as String,
                        document.get("nombre") as String,
                        document.get("cantidadDisponible") as Long,
                        document.get("precioUnitario") as Long,
                        document.get("categoria") as String,
                        document.get("fotografia") as String,
                        document.get("observaciones") as String
                    )
                }

                if (producto != null) {
                    productos.add(producto)
                }
            }
        }

        return productos.sortedWith(compareBy { it.id })
    }
}