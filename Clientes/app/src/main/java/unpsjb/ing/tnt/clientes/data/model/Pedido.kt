package unpsjb.ing.tnt.clientes.data.model

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Pedido(
    var id: String,
    var tienda: String,
    var estado: String,
    var usuario: String,
    var total: Double,
    var envio: Double,
    var propina: Double,
    var comision: Double,
    var direccion: String,
    var metodoDePago: MetodoDePago,
    var estampaDeTiempo: Timestamp,
    var productos: ArrayList<Producto>,
) {
    fun valido(): Boolean {
        return true
    }

    fun guardar(): Task<DocumentReference> {
        return FirebaseFirestore.getInstance().collection("pedidos")
            .add(this.getToSave())
    }

    private fun getToSave(): HashMap<String, Any> {
        return hashMapOf(
            "tienda" to tienda,
            "estado" to estado,
            "usuario" to usuario,
            "total" to total,
            "envio" to envio,
            "propina" to propina,
            "comision" to comision,
            "direccion" to direccion,
            "metodoDePago" to metodoDePago,
            "estampaDeTiempo" to estampaDeTiempo,
            "productos" to productos.map { it.getToSave() }
        )
    }

    companion object {
        fun new(
            tienda: String = "",
            estado: String = "",
            usuario: String = "",
            total: Double = 0.0,
            envio: Double = 0.0,
            propina: Double = 0.0,
            comision: Double = 0.0,
            direccion: String = "",
            metodoDePago: MetodoDePago = MetodoDePago.new(),
            productos: ArrayList<Producto> = arrayListOf()
        ): Pedido {
            return Pedido(
                "",
                tienda,
                estado,
                usuario,
                total,
                envio,
                propina,
                comision,
                direccion,
                metodoDePago,
                Timestamp.now(),
                productos
            )
        }
    }
}