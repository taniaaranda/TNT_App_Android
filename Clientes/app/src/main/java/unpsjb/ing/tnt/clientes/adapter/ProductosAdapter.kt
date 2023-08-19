package unpsjb.ing.tnt.clientes.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Producto
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.cargandoStock

class ProductosAdapter(
    private val context: Context,
    private val productos: List<Producto>,
    private val productosCarrito: List<ProductoCarrito>,
    private val callbackAgregar: (producto: Producto) -> Unit,
    private val callbackQuitar: (producto: Producto) -> Unit
): RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {
    private lateinit var productoViewHolder: ProductoViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.item_producto, parent, false
        )

        return ProductoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        productoViewHolder = holder

        holder.nombreProducto.text = productos[position].nombre
        holder.precioProducto.text = productos[position].precioForView()

        cargarCantidad(holder, position)

        productos[position].getBitmapImage().addOnSuccessListener {
            holder.fotoProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(it, 0, it.size)
            )
        }.addOnFailureListener {
            val default = productos[position].getDefaultImage()
            holder.fotoProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(default, 0, default.size)
            )
        }

        hayStock(holder, (productos[position].stock.toString().toInt() != 0))

        setExcesos(holder, productos[position])
        setAgregarAlCarritoListener(holder, position)
        setQuitarDelCarritoListener(holder, position)
        cargandoStock(cargandoStock)
    }

    private fun setExcesos(holder: ProductoViewHolder, producto: Producto) {
        holder.excesoAzucares.visibility = if (producto.excesoAzucar) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.excesoSodio.visibility = if (producto.excesoSodio) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.excesoCalorias.visibility = if (producto.excesoCalorias) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.excesoGrasasTotales.visibility = if (producto.excesoGrasasTot) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.excesoGrasasSaturadas.visibility = if (producto.excesoGrasasSat) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun cargarCantidad(holder: ProductoViewHolder, position: Int) {
        if (productosCarrito.isNotEmpty()) {
            try {
                val producto = productosCarrito.first { it.producto.id == productos[position].id }
                holder.cantidadAgregada.text = producto.cantidad.toString()
            } catch (e: NoSuchElementException) {
                holder.cantidadAgregada.text = "0"
            }
        }
    }

    private fun setAgregarAlCarritoListener(holder: ProductoViewHolder, position: Int) {
        holder.botonAgregar.setOnClickListener {
            cargandoStock = true
            incrementarCantidad(productos[position])
        }
    }

    private fun incrementarCantidad(producto: Producto) {
        producto.decrementarStock()

        producto.guardar()
            .addOnSuccessListener {
                callbackAgregar(producto)
                cargandoStock = false
            }
            .addOnFailureListener {
                cargandoStock = false
            }
    }

    private fun setQuitarDelCarritoListener(holder: ProductoViewHolder, position: Int) {
        holder.botonQuitar.setOnClickListener {
            cargandoStock = true

            val productoCarrito = carritoTieneProducto(position)

            if (productoCarrito == null || productoCarrito.cantidad <= 0) {
                cargandoStock = false
                return@setOnClickListener
            }

            decrementarCantidad(productos[position])
        }
    }

    private fun carritoTieneProducto(position: Int): ProductoCarrito? {
        return productosCarrito
            .firstOrNull { it.producto.id == productos[position].id }
    }

    private fun decrementarCantidad(producto: Producto) {
        producto.incrementarStock()

        val cantidadActual = productoViewHolder.cantidadAgregada.text.toString().toInt()

        if (cantidadActual == 0) {
            cargandoStock = false
            return
        }

        producto.guardar()
            .addOnSuccessListener {
                callbackQuitar(producto)
                productoViewHolder.cantidadAgregada.text =
                    productoViewHolder.cantidadAgregada.text.toString().toInt().dec().toString()
                cargandoStock = false
            }
            .addOnFailureListener {
                cargandoStock = false
            }
    }

    private fun hayStock(holder: ProductoViewHolder, hay: Boolean) {
        if (hay) {
            holder.sinStock.visibility = View.GONE
            holder.cargandoStock.visibility = View.GONE
            holder.controlStock.visibility = View.VISIBLE
        } else {
            holder.sinStock.visibility = View.VISIBLE
            holder.cargandoStock.visibility = View.GONE
            holder.controlStock.visibility = View.GONE
        }
    }

    private fun cargandoStock(cargando: Boolean) {
        if (cargando) {
            productoViewHolder.controlStock.visibility = View.GONE
            productoViewHolder.cargandoStock.visibility = View.VISIBLE
        } else {
            productoViewHolder.cargandoStock.visibility = View.GONE
            productoViewHolder.controlStock.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto: TextView = itemView.findViewById(R.id.nombre_producto)
        val precioProducto: TextView = itemView.findViewById(R.id.precio_producto_item)
        val fotoProducto: ImageView = itemView.findViewById(R.id.imagen_producto)
        val botonAgregar: TextView = itemView.findViewById(R.id.agregar_producto)
        val botonQuitar: TextView = itemView.findViewById(R.id.quitar_producto)
        val cantidadAgregada: TextView = itemView.findViewById(R.id.cuenta_productos)

        val excesoAzucares: ImageView = itemView.findViewById(R.id.exceso_azucares)
        val excesoSodio: ImageView = itemView.findViewById(R.id.exceso_sodio)
        val excesoCalorias: ImageView = itemView.findViewById(R.id.exceso_calorias)
        val excesoGrasasTotales: ImageView = itemView.findViewById(R.id.exceso_grasas_totales)
        val excesoGrasasSaturadas: ImageView = itemView.findViewById(R.id.exceso_grasas_saturadas)

        val controlStock: View = itemView.findViewById(R.id.controles_stock)
        val cargandoStock: View = itemView.findViewById(R.id.loading_stock)
        val sinStock: TextView = itemView.findViewById(R.id.sin_stock)
    }
}