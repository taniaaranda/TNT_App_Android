package unpsjb.ing.tnt.clientes.data.model

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Carrito(
    val id: String,
    val usuario: String,
    val productos: ArrayList<ProductoCarrito>,
    val fechaCreacion: Timestamp,
    val total: Long,
    val activo: Boolean
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
}