package unpsjb.ing.tnt.clientes.data.model

import com.google.firebase.Timestamp

class Pedido(
    val id: String,
    val estado: String,
    val estampaDeTiempo: Timestamp,
    val productos: ArrayList<String>
)