package unpsjb.ing.tnt.vendedores.ui.home

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
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.VendedoresApplication
import unpsjb.ing.tnt.vendedores.adapter.PedidosAdapter
import unpsjb.ing.tnt.vendedores.data.model.Pedido
import unpsjb.ing.tnt.vendedores.databinding.FragmentHomeBinding
import java.util.ArrayList

private const val PEDIDOS_COLLECTION_NAME = "pedidos"

class HomeFragment : FirebaseConnectedFragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var listView: View
    private lateinit var fragmentContext: Context
    private var pedidosSnapshotListener: ListenerRegistration? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        fragmentContext = this.requireContext()
        listView = binding.root

        prepareSpinner()
        registerPedidosSnapshotListener()

        return listView
    }

    private fun prepareSpinner() {
        val estadosAdapter = ArrayAdapter(this.requireContext(),
            android.R.layout.simple_spinner_item, Pedido.getStateValues())
        binding.filtroEstados.adapter = estadosAdapter

        binding.filtroEstados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(fragmentContext, "Debes seleccionar algún filtro", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                registerPedidosSnapshotListener()
            }
        }
    }

    private fun registerPedidosSnapshotListener() {
        pedidosSnapshotListener?.remove()

        var selectedFilter = binding.filtroEstados.selectedItem

        if (selectedFilter == null) {
            binding.filtroEstados.setSelection(0)
            selectedFilter = binding.filtroEstados.selectedItem
        }

        pedidosSnapshotListener = getDbReference().collection(PEDIDOS_COLLECTION_NAME)
            .whereEqualTo("tienda", VendedoresApplication.tienda!!.id)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ListadoPedidos", e.message.toString())
                    return@addSnapshotListener
                }

                val adapter = PedidosAdapter(this.requireContext(), parsePedidos(snapshots, selectedFilter as String))
                binding.listadoPedidos.adapter = adapter
            }
    }

    private fun parsePedidos(snapshots: QuerySnapshot?, filtro: String): List<Pedido> {
        val pedidos = ArrayList<Pedido>()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                if (!Pedido.validateDocument(document)) {
                    continue
                }

                val estado = document.get("estado") as String

                if (filtro != "Todos" && estado != Pedido.getKeyByState(filtro)) {
                    continue
                }

                pedidos.add(
                    Pedido(
                    document.id,
                    document.get("productos") as ArrayList<String>,
                    Pedido.getStateByKey(document.get("estado") as String),
                    document.get("estampaDeTiempo") as Timestamp
                ))
            }
        }

        return pedidos.sortedWith(compareBy { it.id })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}