package unpsjb.ing.tnt.clientes.ui.pedidos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.PedidosAdapter
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.databinding.FragmentPedidosBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class PedidosFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentPedidosBinding
    private lateinit var pedidosView: View
    private var pedidosSnapshotListener: ListenerRegistration? = null

    private var filtro: String = "Pendiente"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pedidos, container, false
        )

        pedidosView = binding.root

        setFiltro()
        setPedidosListener()

        return pedidosView
    }

    private fun setFiltro() {
        binding.filtro.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Pedido.getStateValues()
        )

        binding.filtro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view != null) {
                    filtro = (view as TextView).text.toString()
                    setPedidosListener()
                }
            }
        }
    }

    private fun setPedidosListener() {
        pedidosSnapshotListener?.remove()

        pedidosSnapshotListener = FirebaseFirestore.getInstance().collection("pedidos")
            .whereEqualTo("usuario", FirebaseAuth.getInstance().currentUser!!.uid)
            .whereEqualTo("estado", Pedido.getKeyByState(filtro))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                var pedidos = listOf<Pedido>()
                if (snapshot != null && snapshot.documents.size > 0) {
                    pedidos = snapshot.documents.map { Pedido.hidratar(it) }
                }

                binding.pedidos.adapter = PedidosAdapter(
                    requireContext(),
                    pedidos
                )
            }
    }
}