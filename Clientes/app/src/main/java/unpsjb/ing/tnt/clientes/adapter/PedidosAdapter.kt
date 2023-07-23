package unpsjb.ing.tnt.clientes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Pedido
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

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pedido_item, parent, false
        )

        FirebaseFirestore.getInstance().collection("tiendas")
            .document(dataSource[position].tienda)
            .get()
            .addOnSuccessListener {
                binding.errorLayout.visibility = View.GONE
                binding.pedidoItemLayout.visibility = View.VISIBLE
                binding.nombreTienda.text = it.get("nombre").toString()
                binding.estadoPedido.text = dataSource[position].estado
                binding.fechaCreacion.text = DateFormat.format("dd/MM/yyyy hh:mm:ss", dataSource[position].estampaDeTiempo.toDate())
                binding.cantidadProductos.text = dataSource[position].productos.sumOf { productoCarrito ->
                    productoCarrito.cantidad
                }.toString()
                binding.precio.text = dataSource[position].total.toString()
            }
            .addOnFailureListener {
                binding.pedidoItemLayout.visibility = View.GONE
                binding.errorLayout.visibility = View.VISIBLE
            }

        binding.pedidoItemLayout.setOnClickListener {
            parent!!.findNavController().navigate(R.id.nav_order, bundleOf(
                "pedido" to dataSource[position].id
            ))
        }

        return binding.root
    }
}