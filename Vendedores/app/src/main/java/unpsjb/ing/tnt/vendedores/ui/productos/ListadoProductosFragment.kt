package unpsjb.ing.tnt.vendedores.ui.productos

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.VendedoresApplication
import unpsjb.ing.tnt.vendedores.adapter.ProductosAdapter
import unpsjb.ing.tnt.vendedores.data.model.Producto
import unpsjb.ing.tnt.vendedores.databinding.FragmentProductosBinding
import java.util.ArrayList
import java.util.Locale

private const val PRODUCTOS_COLLECTION_NAME = "productos"

class ListadoProductosFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentProductosBinding
    private lateinit var fragmentContext: Context
    private lateinit var listView: View

    private var filtroNombre: String? = null

    private lateinit var productos: ArrayList<Producto>
    private lateinit var recyclerView: RecyclerView
    private lateinit var productosAdapter: ProductosAdapter

    private var productosSnapshotListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_productos, container, false
        )

        fragmentContext = this.requireContext()
        listView = binding.root

        recyclerView = listView.findViewById(R.id.productos)
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext()
        )

        setupView()

        return listView
    }

    private fun setupView() {
        productos = arrayListOf()
        productosAdapter = ProductosAdapter(requireContext(), productos)
        recyclerView.adapter = productosAdapter

        registerButtonAction()
        setFilterListener()
        registerProductoSnapshotListener()
    }

    private fun registerButtonAction() {
        val btnAltaProducto = listView.findViewById<FloatingActionButton>(
            R.id.btn_alta_producto
        )

        btnAltaProducto.setOnClickListener{ findNavController().navigate(R.id.product_new) }
    }

    private fun setFilterListener() {
        binding.nombreFiltro.doOnTextChanged { text, start, before, count ->
            filtroNombre = if (text == "") {
                null
            } else {
                text.toString()
            }

            registerProductoSnapshotListener()
        }
    }

    private fun registerProductoSnapshotListener() {
        productosSnapshotListener?.remove()

        productosSnapshotListener = getDbReference().collection(PRODUCTOS_COLLECTION_NAME)
            .whereEqualTo("tienda", VendedoresApplication.tienda!!.id)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ListadoProductos", e.message.toString())
                    return@addSnapshotListener
                }

                handleProductos(snapshots)
            }
    }

    fun handleProductos(snapshots: QuerySnapshot?) {
        productos.clear()

        if (snapshots != null){
            for (document in snapshots.documents) {
                val producto = Producto.hidratar(document)

                if ((filtroNombre != null &&
                    filtroNombre != "" &&
                    !producto.nombre.lowercase(Locale.ROOT).contains(filtroNombre.toString())) ||
                    (productos.contains(producto))
                ) {
                    continue
                }

                productos.add(producto)
            }

            handleUpdateProductosDataset()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun handleUpdateProductosDataset() {
        productosAdapter.notifyDataSetChanged()
    }
}