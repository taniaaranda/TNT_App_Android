package unpsjb.ing.tnt.clientes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.databinding.FragmentPedidoItemBinding

class PedidosAdapter(
    context: Context,
    private val dataSource: List<Pedido>
): BaseAdapter() {
    private lateinit var binding: FragmentPedidoItemBinding
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Pedido {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pedido_item, parent, false
        )

        binding.pedido = getItem(position)

        binding.pedidoItemLayout.setOnClickListener {
            parent!!.findNavController().navigate(R.id.nav_order, bundleOf(
                "pedido" to dataSource[position].id
            ))
        }

        return binding.root
    }

    private fun getCastedTienda(documentSnapshot: DocumentSnapshot): Tienda {
        return Tienda(
            documentSnapshot.id,
            documentSnapshot.get("nombre") as String,
            documentSnapshot.get("rubro") as String,
            documentSnapshot.get("calle") as String,
            documentSnapshot.get("ubicacionLatLong") as HashMap<String, Double>,
            documentSnapshot.get("horario_de_atencion") as HashMap<String, String>,
            documentSnapshot.get("metodos_de_pago") as ArrayList<String>
        )
    }
}