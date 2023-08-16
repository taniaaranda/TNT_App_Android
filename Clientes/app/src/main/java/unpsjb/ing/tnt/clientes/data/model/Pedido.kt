package unpsjb.ing.tnt.clientes.data.model

import android.text.format.DateFormat
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

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
    var productos: ArrayList<ProductoCarrito>,
) {
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

    fun valido(): Boolean {
        if (
            tienda.isEmpty() ||
            estado.isEmpty() ||
            usuario.isEmpty() ||
            total == 0.0 ||
            envio == 0.0 ||
            propina == 0.0 ||
            comision == 0.0 ||
            direccion.isEmpty() ||
            !metodoDePago.esValido() ||
            (metodoDePago.tipo == MetodoDePago.TIPO_TARJETA && !metodoDePago.datos.containsKey("cuotas")) ||
            productos.isEmpty()
        ) {
            return false
        }

        return true
    }

    fun nombreTienda(): String {
        return FirebaseFirestore.getInstance().collection("tiendas")
            .document(tienda)
            .get().result.get("nombre").toString()
    }

    fun fechaCreacionForView(): CharSequence? {
        return DateFormat.format("dd/MM/yyyy hh:mm:ss", estampaDeTiempo.toDate())
    }

    fun cantidadProductosForView(): String {
        return productos.sumOf { it.cantidad }.toInt().toString()
    }

    fun totalForView(): String {
        return total.toString()
    }

    companion object {
        fun new(
            tienda: String = "",
            estado: String = "PENDIENTE",
            usuario: String = "",
            total: Double = 0.0,
            envio: Double = 0.0,
            propina: Double = 0.0,
            comision: Double = 0.0,
            direccion: String = "",
            metodoDePago: MetodoDePago = MetodoDePago.new(),
            productos: ArrayList<ProductoCarrito> = arrayListOf()
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