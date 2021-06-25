package unpsjb.ing.tnt.vendedores

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.vendedores.adapter.ProductoAdapter
import unpsjb.ing.tnt.vendedores.data.model.Pedido
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

        prepareSpinner()
        registerProductoSnapshotListener()

        return listView
    }

    private fun prepareSpinner() {
        val estadosAdapter = ArrayAdapter(this.requireContext(),
            android.R.layout.simple_spinner_item, Producto.getStateValues())
        binding.filtroEstados.adapter = estadosAdapter

        binding.filtroEstados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(fragmentContext, "Debes seleccionar algún filtro", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                registerProductoSnapshotListener()
            }
        }
    }

    private fun registerProductoSnapshotListener() {
        productosSnapshotListener?.remove()

        var selectedFilter = binding.filtroEstados.selectedItem

        if (selectedFilter == null) {
            binding.filtroEstados.setSelection(0)
            selectedFilter = binding.filtroEstados.selectedItem
        }

        productosSnapshotListener = getDbReference().collection(PRODUCTOS_COLLECTION_NAME)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ListadoProductos", e.message.toString())
                    return@addSnapshotListener
                }

                val adapter = ProductoAdapter(this.requireContext(), parseProducto(snapshots, selectedFilter as String))
                binding.listadoProductos.adapter = adapter
            }
    }

    private fun parseProducto(snapshots: QuerySnapshot?, filtro: String): List<Producto> {
        val productos = ArrayList<Producto>()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                val estado = document.get("estado") as String

                if (filtro != "Todos" && estado != Producto.getKeyByState(filtro)) {
                    continue
                }

                productos.add(Producto(
                    document.get("id") as String,
                    document.get("nombre") as ArrayList<String>,
                    Producto.getStateByKey(document.get("estado") as String),
                    document.get("estampaDeTiempo") as Timestamp
                    ))
            }
        }
        return productos.sortedWith(compareBy { it.id })
    }
}