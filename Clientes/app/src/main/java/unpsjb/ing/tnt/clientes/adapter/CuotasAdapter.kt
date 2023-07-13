package unpsjb.ing.tnt.clientes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.clientes.R

class CuotasAdapter(
    private val context: Context,
    private val monto: Double,
    private val dataSource: List<HashMap<String, Double>>
): RecyclerView.Adapter<CuotasAdapter.CuotasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuotasViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.cuota_item, parent, false
        )

        return CuotasViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: CuotasViewHolder, position: Int) {
        val numeroCuota = dataSource[position]["numero"]!!.toInt()
        val cft = dataSource[position]["cft"]!!

        holder.numeroCuota.text = numeroCuota.toString()
        holder.cft.text = cft.toString()
        holder.valorAPagar.text = ((cft / 100) * monto).toString()
    }

    class CuotasViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val numeroCuota: TextView = itemView.findViewById(R.id.numero_cuota)
        val valorAPagar: TextView = itemView.findViewById(R.id.valor_a_pagar)
        val cft: TextView = itemView.findViewById(R.id.cft)
    }
}