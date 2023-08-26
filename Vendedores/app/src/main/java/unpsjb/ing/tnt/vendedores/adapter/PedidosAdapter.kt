package unpsjb.ing.tnt.vendedores.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.data.model.Pedido
import unpsjb.ing.tnt.vendedores.databinding.ItemPedidoBinding

class PedidosAdapter(private val context: Context, private val dataSource: List<Pedido>): BaseAdapter() {
    private lateinit var binding: ItemPedidoBinding
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.item_pedido, parent, false
        )

        binding.pedido = dataSource[position]

        binding.pedidoItemLayout.setOnClickListener {
            parent!!.findNavController().navigate(R.id.nav_order, bundleOf(
                "pedido" to dataSource[position].id
            ))
        }

        return binding.root
    }
}