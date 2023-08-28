package unpsjb.ing.tnt.vendedores.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.data.model.Producto

class ProductosAdapter(
    private val context: Context,
    private val productos: ArrayList<Producto>
): RecyclerView.Adapter<ProductosAdapter.ProductosViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductosViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.item_producto, parent, false
        )

        return ProductosViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductosViewHolder, position: Int) {
        holder.nombreProducto.text = productos[position].nombre
        holder.descripcionProducto.text = productos[position].observaciones
        holder.precioProducto.text = "$" + productos[position].precio.toString()
        holder.stockProducto.text = "Stock: " + productos[position].stock.toString()

        productos[position].getBitmapImage().addOnSuccessListener {
            holder.imagenProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(it, 0, it.size)
            )
        }.addOnFailureListener {
            val default = productos[position].getDefaultImage()
            holder.imagenProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(default, 0, default.size)
            )
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ProductosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imagenProducto: ImageView = itemView.findViewById(R.id.imagen_producto)
        val nombreProducto: TextView = itemView.findViewById(R.id.nombre_producto)
        val descripcionProducto: TextView = itemView.findViewById(R.id.descripcion_producto)
        val stockProducto: TextView = itemView.findViewById(R.id.stock_producto)
        val precioProducto: TextView = itemView.findViewById(R.id.precio_producto)
    }
}