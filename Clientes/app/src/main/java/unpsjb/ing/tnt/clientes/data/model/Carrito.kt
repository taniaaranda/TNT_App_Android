package unpsjb.ing.tnt.clientes.data.model

import android.annotation.SuppressLint
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Carrito(
    var id: String,
    var usuario: String,
    var productos: ArrayList<ProductoCarrito>,
    var fechaCreacion: Timestamp,
    var activo: Boolean,
    var tienda: String
) {
    @SuppressLint("SimpleDateFormat")
    fun getFechaCreacionForView(): String {
        val milliseconds = fechaCreacion.seconds * 1000 + fechaCreacion.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val netDate = Date(milliseconds)

        return "Fecha: " + sdf.format(netDate).toString()
    }

    fun getTotal(): String {
        return "$" + productos.sumOf {
            it.producto.precio * it.cantidad
        }.toString()
    }

    fun estaGuardado(): Boolean {
        return id.isNotEmpty()
    }

    fun estaVacio(): Boolean {
        return productos.isEmpty()
    }

    fun guardar(listener: OnCompleteListener<DocumentReference>): Task<out Any> {
        return getConnection().add(this.getToSave()).addOnCompleteListener(listener)
    }

    fun actualizar(listener: OnCompleteListener<Void>): Task<Void> {
        return getConnection().document(id).update("productos", productos.map { it.getToSave() })
            .addOnCompleteListener(listener)
    }

    fun agregarAlCarrito(producto: Producto) {
        try {
            val productoCarrito = productos.first { it.producto.id == producto.id }
            val indexOf = productos.indexOf(productoCarrito)
            productoCarrito.cantidad += 1
            productos[indexOf] = productoCarrito
        } catch (e: NoSuchElementException) {
            productos.add(ProductoCarrito(producto, 1))
        }
    }

    fun quitarDelCarrito(producto: Producto) {
        try {
            val productoCarrito = productos.first { it.producto.id == producto.id }
            val indexOf = productos.indexOf(productoCarrito)
            productoCarrito.cantidad -= 1

            if (productoCarrito.cantidad > 0) {
                productos[indexOf] = productoCarrito
            } else {
                productos.removeAt(indexOf)
            }
        } catch (e: NoSuchElementException) { }
    }

    fun getCantidadTotal(): Int {
        if (productos.isNotEmpty()) {
            return productos.sumOf { it.cantidad }.toInt()
        }

        return 0
    }

    fun getPropinaSugerida(): Double {
        if (productos.isNotEmpty()) {
            return productos.sumOf {
                it.cantidad * it.producto.precio
            } * 0.030
        }

        return 0.0
    }

    fun getToSave(): HashMap<String, Any> {
        return hashMapOf(
            "usuario" to usuario,
            "productos" to productos.map { it.getToSave() },
            "fecha_creacion" to fechaCreacion,
            "activo" to activo,
            "tienda" to tienda
        )
    }

    companion object {
        const val COLLECTION_NAME = "carritos"

        fun getConnection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
        }

        fun new(usuarioId: String, tiendaId: String): Carrito {
            return Carrito(
                "",
                usuarioId,
                arrayListOf(),
                Timestamp.now(),
                true,
                tiendaId
            )
        }

        fun hidratar(document: HashMap<String, Any>): Carrito {
            val productosDelCarrito: ArrayList<ProductoCarrito> = arrayListOf()

            for (productoCarrito in document["productos"] as ArrayList<HashMap<String, Any>>) {
                productosDelCarrito.add(
                    ProductoCarrito.hidratar(productoCarrito)
                )
            }

            return Carrito(
                document["id"] as String,
                document["usuario"] as String,
                productosDelCarrito,
                document["fecha_creacion"] as Timestamp,
                document["activo"] as Boolean,
                document["tienda"] as String
            )
        }

        fun hidratar(document: DocumentSnapshot): Carrito {
            return Carrito(
                document.id,
                document.get("usuario") as String,
                (document.get("productos") as ArrayList<HashMap<String, Any>>).map {
                    ProductoCarrito.hidratar(it)
                } as ArrayList<ProductoCarrito>,
                document.get("fecha_creacion") as Timestamp,
                document.get("activo") as Boolean,
                document.get("tienda") as String
            )
        }
    }
}