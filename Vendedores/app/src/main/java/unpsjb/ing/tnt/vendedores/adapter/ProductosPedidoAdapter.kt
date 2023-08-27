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
import unpsjb.ing.tnt.vendedores.data.model.ProductoCarrito
import unpsjb.ing.tnt.vendedores.R

class ProductosPedidoAdapter(
    private val context: Context,
    private val productos: List<ProductoCarrito>
): RecyclerView.Adapter<ProductosPedidoAdapter.ProductosPedidoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosPedidoViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.item_producto_pedido, parent, false
        )

        return ProductosPedidoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductosPedidoViewHolder, position: Int) {
        holder.nombreProducto.text = productos[position].producto.nombre
        holder.descripcionProducto.text = productos[position].producto.observaciones
        holder.precioProducto.text = "$" + productos[position].producto.precio.toString()
        holder.cantidadProducto.text = "Cantidad: " + productos[position].cantidad

        productos[position].producto.getBitmapImage().addOnSuccessListener {
            holder.imagenProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(it, 0, it.size)
            )
        }.addOnFailureListener {
            val default = productos[position].producto.getDefaultImage()
            holder.imagenProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(default, 0, default.size)
            )
        }
    }

    class ProductosPedidoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imagenProducto: ImageView = itemView.findViewById(R.id.imagen_producto)
        val nombreProducto: TextView = itemView.findViewById(R.id.nombre_producto)
        val descripcionProducto: TextView = itemView.findViewById(R.id.descripcion_producto)
        val precioProducto: TextView = itemView.findViewById(R.id.precio_producto)
        val cantidadProducto: TextView = itemView.findViewById(R.id.cantidad_producto)
    }
}