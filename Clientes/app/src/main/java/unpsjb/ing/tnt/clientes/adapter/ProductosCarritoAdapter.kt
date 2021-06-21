package unpsjb.ing.tnt.clientes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Producto
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import unpsjb.ing.tnt.clientes.databinding.CarritoProductoItemBinding

class ProductosCarritoAdapter(private val context: Context, private val dataSource: List<ProductoCarrito>): BaseAdapter() {
    private lateinit var binding: CarritoProductoItemBinding
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): ProductoCarrito {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.carrito_producto_item, parent, false
        )

        val productoCarrito = getItem(position)
        binding.producto = productoCarrito
        binding.imagenProducto.setImageBitmap(productoCarrito.getBitmapImage())

        return binding.root
    }
}