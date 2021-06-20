package unpsjb.ing.tnt.vendedores.data.model

import com.google.firebase.Timestamp

class Pedido(val id: String, val productos: ArrayList<String>, val estado: String, val estampaDeTiempo: Timestamp) {
    fun getFormattedId(): String {
        return "Pedido #$id"
    }

    fun getFormattedTimestamp(): String {
        return estampaDeTiempo.toDate().toString()
    }
}
