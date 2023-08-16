package unpsjb.ing.tnt.clientes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.databinding.TiendaWidgetBinding

class TiendasAdapter (
    private val context: Context,
    private val dataSource: List<Tienda>,
    private val callbackVerTienda: (tiendaId: String) -> Unit,
): BaseAdapter() {
    private lateinit var binding: TiendaWidgetBinding
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    private fun getCastedItem(position: Int): Tienda {
        val item = getItem(position) as Tienda

        return Tienda(
            item.id,
            item.nombre,
            item.rubro,
            item.calle,
            item.ubicacion,
            item.horarioDeAtencion,
            item.metodosDePago
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.tienda_widget, parent, false
        )

        val tienda = getCastedItem(position)
        binding.tienda = tienda

        setCerrado(parent, context, tienda)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ViewHolder", "UseCompatLoadingForDrawables")
    private fun setCerrado(parent: ViewGroup?, context: Context, tienda: Tienda) {
        if (tienda.estaAbierto()) {
            binding.tiendaContainer.background =
                context.getDrawable(R.drawable.rounded_corners_lightgray)

            binding.tiendaContainer.setOnClickListener {
                callbackVerTienda(tienda.id)
            }
        } else {
            binding.tiendaContainer.background =
                context.getDrawable(R.drawable.rounded_corners_darkgray)
            binding.horarioCierre.setTextColor(parent!!.resources.getColor(R.color.quantum_googred700))

            binding.tiendaContainer.setOnClickListener {
                Toast.makeText(context, "El local esta cerrado!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}