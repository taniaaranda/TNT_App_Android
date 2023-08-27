package unpsjb.ing.tnt.clientes.data.model

import android.text.format.DateFormat
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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
        const val ESTADO_PENDIENTE = "PENDIENTE"
        const val ESTADO_EN_PREPARACION = "EN_PREPARACION"
        const val ESTADO_COMPLETADO = "COMPLETADO"
        const val ESTADO_CANCELADO = "CANCELADO"

        private val STATES = mapOf(
            ESTADO_PENDIENTE to "Pendiente",
            ESTADO_EN_PREPARACION to "En Curso",
            ESTADO_COMPLETADO to "Completado",
            ESTADO_CANCELADO to "Cancelado"
        )

        fun getStateKeys(): Array<String> {
            return STATES.keys.toTypedArray()
        }

        fun getStateValues(): Array<String> {
            return STATES.values.toTypedArray()
        }

        fun getKeyByState(state: String): String {
            return STATES.filter { state == it.value }.keys.first()
        }

        fun getStateByKey(key: String): String {
            return STATES[key]!!
        }

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

        fun hidratar(document: DocumentSnapshot) = Pedido(
            document.id,
            document.get("tienda").toString(),
            document.get("estado").toString(),
            document.get("usuario").toString(),
            document.get("total").toString().toDouble(),
            document.get("envio").toString().toDouble(),
            document.get("propina").toString().toDouble(),
            document.get("comision").toString().toDouble(),
            document.get("direccion").toString(),
            MetodoDePago(
            (document.get("metodoDePago") as HashMap<String, Any>)["tipo"].toString(),
            (document.get("metodoDePago") as HashMap<String, Any>)["datos"] as HashMap<String, Any>
            ),
            document.get("estampaDeTiempo") as Timestamp,
            parseProductos(document.get("productos") as ArrayList<HashMap<String, Any>>)
        )

        fun parseProductos(productosCarrito: java.util.ArrayList<HashMap<String, Any>>): java.util.ArrayList<ProductoCarrito> {
            val productos: ArrayList<ProductoCarrito> = arrayListOf()

            for (producto in productosCarrito) {
                productos.add(ProductoCarrito(
                    Producto(
                        (producto["producto"] as HashMap<String, Any>)["id"].toString(),
                        (producto["producto"] as HashMap<String, Any>)["nombre"].toString(),
                        (producto["producto"] as HashMap<String, Any>)["observaciones"].toString(),
                        (producto["producto"] as HashMap<String, Any>)["stock"].toString().toLong(),
                        (producto["producto"] as HashMap<String, Any>)["precio"].toString().toLong(),
                        (producto["producto"] as HashMap<String, Any>)["categoria"].toString(),
                        (producto["producto"] as HashMap<String, Any>)["foto"].toString(),
                        (producto["producto"] as HashMap<String, Any>)["tienda"].toString(),
                        (producto["producto"] as HashMap<String, Any>)["excesoSodio"].toString().toBoolean(),
                        (producto["producto"] as HashMap<String, Any>)["excesoGrasasTot"].toString().toBoolean(),
                        (producto["producto"] as HashMap<String, Any>)["excesoGrasasSat"].toString().toBoolean(),
                        (producto["producto"] as HashMap<String, Any>)["excesoCalorias"].toString().toBoolean(),
                        (producto["producto"] as HashMap<String, Any>)["excesoAzucar"].toString().toBoolean()
                    ),
                    producto["cantidad"].toString().toLong()
                ))
            }

            return productos
        }
    }
}