package unpsjb.ing.tnt.clientes.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.databinding.TiendaWidgetBinding
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.carrito

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

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.tienda_widget, parent, false
        )

        val tienda = getCastedItem(position)
        binding.tienda = tienda

        binding.tiendaContainer.setOnClickListener {
            callbackVerTienda(tienda.id)
        }

        return binding.root
    }
}