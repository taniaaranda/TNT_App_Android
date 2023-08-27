package unpsjb.ing.tnt.vendedores.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.data.model.Producto
import unpsjb.ing.tnt.vendedores.databinding.ItemProductoBinding

class ProductoAdapter(
    private val context: Context,
    private val dataSource: List<Producto>
): BaseAdapter() {
    private lateinit var binding: ItemProductoBinding
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Producto {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.item_producto, parent, false
        )

        binding.producto = dataSource[position]
        Log.i("position", position.toString())

        dataSource[position].getBitmapImage().addOnSuccessListener {
            binding.imgFoto.setImageBitmap(
                BitmapFactory.decodeByteArray(it, 0, it.size)
            )
        }.addOnFailureListener {
            val default = dataSource[position].getDefaultImage()
            binding.imgFoto.setImageBitmap(
                BitmapFactory.decodeByteArray(default, 0, default.size)
            )
        }

        return binding.root
    }
}