package unpsjb.ing.tnt.vendedores

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import unpsjb.ing.tnt.vendedores.adapter.PedidosAdapter
import unpsjb.ing.tnt.vendedores.data.model.Pedido
import unpsjb.ing.tnt.vendedores.databinding.FragmentListadoPedidosBinding
import java.util.*


private const val PEDIDOS_COLLECTION_NAME = "pedidos"

class ListadoPedidosFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentListadoPedidosBinding
    private lateinit var listView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listado_pedidos, container, false
        )

        listView = binding.root

        getDbReference().collection(PEDIDOS_COLLECTION_NAME)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ListadoPedidos", e.message.toString())
                    return@addSnapshotListener
                }

                Log.d("ListadoPedidos", parsePedidos(snapshots).size.toString())

                val adapter = PedidosAdapter(this.requireContext(), parsePedidos(snapshots))
                binding.listadoPedidos.adapter = adapter
            }

        return listView
    }

    private fun parsePedidos(snapshots: QuerySnapshot?): List<Pedido> {
        val pedidos = ArrayList<Pedido>()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                pedidos.add(
                    Pedido(
                        document.get("id") as String,
                        document.get("productos") as ArrayList<String>,
                        document.get("estado") as String,
                        document.get("estampaDeTiempo") as Timestamp
                    )
                )
            }
        }

        return pedidos.sortedWith(compareBy { it.id })
    }
}