package unpsjb.ing.tnt.vendedores.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.data.model.Producto
import unpsjb.ing.tnt.vendedores.databinding.ItemProductoBinding

class ProductoAdapter(private val context: Context, private val dataSource: List<Producto>): BaseAdapter() {
    private lateinit var binding: ItemProductoBinding
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    private fun getCastedItem(position: Int): Producto {
        val item = getItem(position) as Producto

        return Producto(
            item.id,
            item.nombre,
            item.cantidadDisponible,
            item.precioUnitario,
            item.categoría,
            item.fotografía,
            item.observaciones
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.item_producto, parent, false
        )
        binding.Producto = getCastedItem(position)

        return binding.root
    }

}