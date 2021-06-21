package unpsjb.ing.tnt.clientes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.databinding.TiendaWidgetBinding

class TiendasAdapter (private val context: Context, private val dataSource: List<Tienda>): BaseAdapter(){
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
                item.rubro,
                item.ubicacion,
                item.horario_de_atencion,
                item.metodos_de_pago
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
        binding.tienda = getCastedItem(position)

        return binding.root
    }
}