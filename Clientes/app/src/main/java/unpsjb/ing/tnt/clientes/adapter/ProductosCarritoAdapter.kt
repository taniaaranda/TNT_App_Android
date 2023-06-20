package unpsjb.ing.tnt.clientes.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import java.lang.IndexOutOfBoundsException

class ProductosCarritoAdapter(
    private val context: Context,
    private val dataSource: List<ProductoCarrito>,
    private val callbackAgregar: (producto: ProductoCarrito) -> Unit,
    private val callbackQuitar: (producto: ProductoCarrito) -> Unit
): RecyclerView.Adapter<ProductosCarritoAdapter.ProductoCarritoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoCarritoViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.carrito_producto_item, parent, false
        )

        return ProductoCarritoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: ProductoCarritoViewHolder, position: Int) {
        setData(holder, position)

        setAgregarAlCarritoListener(holder, position)
        setQuitarDelCarritoListener(holder, position)
    }

    private fun setData(holder: ProductoCarritoViewHolder, position: Int) {
        holder.nombreProducto.text = dataSource[position].producto.nombre
        holder.descripcionProducto.text = dataSource[position].producto.observaciones
        holder.categoriaProducto.text = dataSource[position].producto.categoria
        holder.precioProducto.text = dataSource[position].getPrecioForView()
        holder.stockProducto.text = dataSource[position].cantidad.toString()
        setImage(holder, position)
    }

    private fun setImage(holder: ProductoCarritoViewHolder, position: Int) {
        dataSource[position].producto.getBitmapImage().addOnSuccessListener {
            holder.imagenProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(it, 0, it.size)
            )
        }.addOnFailureListener {
            val default = dataSource[position].producto.getDefaultImage()
            holder.imagenProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(default, 0, default.size)
            )
        }
    }

    private fun setAgregarAlCarritoListener(holder: ProductoCarritoViewHolder, position: Int) {
        holder.agregarStock.setOnClickListener {
            cargandoStock(holder, true)

            dataSource[position].producto.decrementarStock()

            dataSource[position].producto.guardar()
                .addOnSuccessListener {
                    callbackAgregar(dataSource[position])
                    cargandoStock(holder, false)
                }
                .addOnFailureListener {
                    cargandoStock(holder, false)
                    // TODO: Mostrar error
                }
        }
    }

    private fun setQuitarDelCarritoListener(holder: ProductoCarritoViewHolder, position: Int) {
        holder.quitarStock.setOnClickListener {
            cargandoStock(holder, true)

            if (holder.stockProducto.text.toString().toInt() == 0) {
                return@setOnClickListener
            }

            dataSource[position].producto.incrementarStock()
            dataSource[position].producto.guardar()
                .addOnSuccessListener {
                    try {
                        callbackQuitar(dataSource[position])
                        cargandoStock(holder, false)
                    } catch (e: IndexOutOfBoundsException) {
                        cargandoStock(holder, false)
                    }  // TODO: Carga de cantidad al modificarla
                }
                .addOnFailureListener {
                    cargandoStock(holder, false)
                    // TODO: Mostrar error
                }
        }
    }

    private fun cargandoStock(holder: ProductoCarritoViewHolder, cargando: Boolean) {
        if (cargando) {
            holder.controlesStock.visibility = View.GONE
            holder.cargando.visibility = View.VISIBLE
        } else {
            holder.cargando.visibility = View.GONE
            holder.controlesStock.visibility = View.VISIBLE
        }
    }

    class ProductoCarritoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imagenProducto: ImageView = itemView.findViewById(R.id.imagen_producto)
        val nombreProducto: TextView = itemView.findViewById(R.id.nombre_producto)
        val descripcionProducto: TextView = itemView.findViewById(R.id.descripcion_producto)
        val categoriaProducto: TextView = itemView.findViewById(R.id.categoria_producto)
        val stockProducto: TextView = itemView.findViewById(R.id.stock)
        val precioProducto: TextView = itemView.findViewById(R.id.precio_producto)
        val agregarStock: TextView = itemView.findViewById(R.id.agregar_stock)
        val quitarStock: TextView = itemView.findViewById(R.id.quitar_stock)
        val controlesStock: ConstraintLayout = itemView.findViewById(R.id.controles_stock)
        val cargando: ConstraintLayout = itemView.findViewById(R.id.loading)
    }
}