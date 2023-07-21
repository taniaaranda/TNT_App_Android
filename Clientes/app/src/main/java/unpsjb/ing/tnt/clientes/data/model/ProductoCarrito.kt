package unpsjb.ing.tnt.clientes.data.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.firebase.firestore.DocumentSnapshot
import unpsjb.ing.tnt.clientes.R

class ProductoCarrito(
    var producto: Producto,
    var cantidad: Long
) {
    fun getPrecioForView(): String {
        return "$${producto.precio.toFloat() * cantidad}"
    }

    fun getToSave(): Any {
        val productoHashMap = producto.getToSave()
        productoHashMap["id"] = producto.id

        return hashMapOf(
            "producto" to productoHashMap,
            "cantidad" to cantidad
        )
    }

    companion object {
        fun hidratar(document: HashMap<String, Any>): ProductoCarrito {
            return ProductoCarrito(
                Producto.hidratar(document["producto"] as HashMap<String, Any>),
                document["cantidad"] as Long
            )
        }

        fun hidratar(document: DocumentSnapshot): ProductoCarrito {
            return ProductoCarrito(
                Producto.hidratar(document["producto"] as HashMap<String, Any>),
                document["cantidad"] as Long
            )
        }
    }
}