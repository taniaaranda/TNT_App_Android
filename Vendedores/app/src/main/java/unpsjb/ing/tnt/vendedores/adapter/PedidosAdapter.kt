package unpsjb.ing.tnt.vendedores.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.data.model.Pedido
import unpsjb.ing.tnt.vendedores.databinding.ItemPedidoBinding
import androidx.navigation.fragment.findNavController

class PedidosAdapter(private val context: Context, private val dataSource: List<Pedido>): BaseAdapter() {
    private lateinit var binding: ItemPedidoBinding
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    private fun getCastedItem(position: Int): Pedido {
        val item = getItem(position) as Pedido

        return Pedido(
            item.id,
            item.productos,
            item.estado,
            item.estampaDeTiempo
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.item_pedido, parent, false
        )
        val pedido = getCastedItem(position)
        binding.pedido = pedido

        val estadoSpinner = binding.root.findViewById<Spinner>(R.id.spinner_estado)
        val states = Pedido.getStateValues().toMutableList()
        states.remove("Todos")
        val estadosAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            states
        )
        estadosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        estadoSpinner.adapter = estadosAdapter

        estadoSpinner.setSelection(estadosAdapter.getPosition(pedido.estado))
        estadoSpinner.tag = pedido.id
        estadoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val dbReference: FirebaseFirestore = Firebase.firestore
                val pedidoId = parent?.tag
                val newState = Pedido.getKeyByState(estadosAdapter.getItem(position))
                if(pedidoId != null){
                    val pedidoRef = dbReference.collection("pedidos").document(pedidoId as String)
                    pedidoRef.update(mapOf("estado" to newState))
                        .addOnSuccessListener {
                            Toast.makeText(context, "¡Estado actualizado!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "No se pudo actualizar el estado", Toast.LENGTH_SHORT).show()
                        }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        return binding.root
    }
}