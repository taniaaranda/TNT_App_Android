package unpsjb.ing.tnt.clientes.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Direccion

class DireccionesCheckoutAdapter(
    private val context: Context,
    private val dataSource: List<Direccion>,
    private val callback: () -> Unit
): RecyclerView.Adapter<DireccionesCheckoutAdapter.DireccionesCheckoutViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DireccionesCheckoutViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.fragment_direccion_checkout_item, parent, false
        )

        return DireccionesCheckoutViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: DireccionesCheckoutViewHolder, position: Int) {
        holder.direccion.text = dataSource[position].calle

        holder.direccionLayout.setOnClickListener {
            ClientesApplication.pedido!!.direccion = dataSource[position].calle

            callback()
        }
    }

    class DireccionesCheckoutViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val direccion: TextView = itemView.findViewById(R.id.texto_direccion)
        val direccionLayout: ConstraintLayout = itemView.findViewById(R.id.direccion_item_layout)
    }
}
