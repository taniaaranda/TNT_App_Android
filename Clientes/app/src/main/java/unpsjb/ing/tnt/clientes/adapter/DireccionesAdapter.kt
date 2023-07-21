package unpsjb.ing.tnt.clientes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Direccion

class DireccionesAdapter(
    private val context: Context,
    private val dataSource: List<Direccion>
): RecyclerView.Adapter<DireccionesAdapter.DireccionesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DireccionesViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.fragment_direccion_item, parent, false
        )

        return DireccionesViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: DireccionesViewHolder, position: Int) {
        holder.direccion.text = dataSource[position].calle

        holder.layout.setOnClickListener {
            holder.itemView.findNavController().navigate(R.id.nav_address, bundleOf(
                "modo" to "editar",
                "direccion" to dataSource[position].id
            ))
        }
    }

    class DireccionesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val direccion: TextView = itemView.findViewById(R.id.direccion)
        val layout: ConstraintLayout = itemView.findViewById(R.id.direccion_layout)
    }
}