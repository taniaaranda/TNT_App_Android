package unpsjb.ing.tnt.clientes.data.model

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Carrito(
    var id: String,
    var usuario: String,
    var productos: ArrayList<ProductoCarrito>,
    var fechaCreacion: Timestamp,
    var total: Long,
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

    fun getTotalForView(): String {
        return "Total: $$total"
    }

    fun actualizarTotal(): Long {
        total = productos.map { it.precio * it.cantidad }.sum()
        return total
    }
}