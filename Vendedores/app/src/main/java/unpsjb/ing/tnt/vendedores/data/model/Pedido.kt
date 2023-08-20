package unpsjb.ing.tnt.vendedores.data.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val STATES = mapOf(
    "todos" to "Todos",
    "PENDIENTE" to "Pendiente",
    "EN_PREPARACION" to "En Preparación",
    "COMPLETADO" to "Completado",
    "CANCELADO" to "Cancelado"
)

class Pedido (val id: String, val productos: ArrayList<String>, val estado: String, val estampaDeTiempo: Timestamp) {
    fun getFormattedId(): String {
        return "Pedido #$id"
    }

    fun getFormattedTimestamp(): String {
        val milliseconds = estampaDeTiempo.seconds * 1000 + estampaDeTiempo.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val netDate = Date(milliseconds)

        return "Fecha: " + sdf.format(netDate).toString()
    }

    companion object {
        fun getStateKeys(): Array<String> {
            return STATES.keys.toTypedArray()
        }

        fun getStateValues(): Array<String> {
            return STATES.values.toTypedArray()
        }

        fun getKeyByState(state: String?): String {
            if (state == null) {
                return STATES["todos"]!!
            }

            return STATES.filter { state == it.value }.keys.first()
        }

        fun getStateByKey(key: String): String {
            return STATES[key]!!
        }

        fun validateDocument(document: DocumentSnapshot?): Boolean {
            var valid = false

            if (document !== null) {
                valid = true

                if (!document.contains("productos")) {
                    Log.e("ValidacionPedido", "El campo 'productos' no esta presente")
                    valid = false
                }

                if (!document.contains("estado")) {
                    Log.e("ValidacionPedido", "El campo 'estado' no esta presente")
                    valid = false
                }

                if (!document.contains("estampaDeTiempo")) {
                    Log.e("ValidacionPedido", "El campo 'estampaDeTiempo' no esta presente")
                    valid = false
                }
            }

            return valid
        }
    }
}
