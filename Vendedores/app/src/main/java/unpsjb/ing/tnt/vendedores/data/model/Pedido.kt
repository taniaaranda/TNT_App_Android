package unpsjb.ing.tnt.vendedores.data.model

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType
import unpsjb.ing.tnt.vendedores.excepciones.TransicionPedidoInvalidException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Pedido (
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
    fun getFormattedId(): String {
        return "Pedido #$id"
    }

    fun totalForView(): String {
        return "$$total"
    }

    fun totalProductos(): Double {
        return productos.sumOf { it.producto.precio * it.cantidad }.toDouble()
    }

    fun getFormattedTimestamp(): String {
        val milliseconds = estampaDeTiempo.seconds * 1000 + estampaDeTiempo.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val netDate = Date(milliseconds)

        return "Fecha: " + sdf.format(netDate).toString()
    }

    fun avanzarEstado(): Boolean {
        val estadoSiguiente = estadoSiguiente()

        if (estadoSiguiente != null && transicionEstadoValida(estadoSiguiente)) {
            cambiarDeEstadoA(estadoSiguiente)
            return true
        }

        return false
    }

    fun retrocederEstado(): Boolean {
        val estadoAnterior = estadoAnterior()

        if (estadoAnterior != null && transicionEstadoValida(estadoAnterior)) {
            cambiarDeEstadoA(estadoAnterior)
            return true
        }

        return false
    }

    fun transicionEstadoValida(estadoNuevo: String): Boolean {
        if (estado == "PENDIENTE" && (estadoNuevo == "EN_PREPARACION" || estadoNuevo == "CANCELADO")) {
            return true
        }

        if (estado == "EN_PREPARACION" && (estadoNuevo == "COMPLETADO" || estadoNuevo == "CANCELADO" || estadoNuevo == "PENDIENTE")) {
            return true
        }

        return false
    }

    fun cambiarDeEstadoA(estadoNuevo: String) {
        if (transicionEstadoValida(estadoNuevo)) {
            estado = estadoNuevo
        } else {
            throw TransicionPedidoInvalidException()
        }
    }

    fun estadoSiguiente(): String? {
        if (estado == ESTADO_PENDIENTE) {
            return ESTADO_EN_PREPARACION
        }

        if (estado == ESTADO_EN_PREPARACION) {
            return ESTADO_COMPLETADO
        }

        return null
    }

    fun estadoAnterior(): String? {
        if (estado == ESTADO_EN_PREPARACION) {
            return ESTADO_PENDIENTE
        }

        if (estado == ESTADO_COMPLETADO) {
            return ESTADO_EN_PREPARACION
        }

        return null
    }

    fun notificarEstado(context: Context) {
        FirebaseFirestore.getInstance().collection("notificaciones")
            .whereEqualTo("usuario", usuario)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty() && it.documents.first().exists()) {
                    val token = it.documents.first().get("token").toString()

                    if (token != "") {
                        notificar(token)
                    } else {
                        // Cliente innotificable
                    }
                } else {
                    // Cliente innotificable
                }
            }
            .addOnFailureListener {
                // Error buscando las notificaciones de la base
            }
    }

    private fun notificar(token: String, context: Context? = null) {
        val payload = "{\"to\": \""+token+"\", \"notification\": {\"title\": \"CoviPedidos\", \"body\": \"Tu pedido se encuentra ahora en estado '"+ getStateByKey(estado)+"'\"}}"

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .header("Authorization", "key=AAAAwLPBMJE:APA91bGkptGo6-QL3arLOospN74Yz3FmeLWfWX9nFDKHBOe4cO8GIGPPvpekQYfvgt3OMaJWUYxMRKdUkOD-mq8HgXnD6aSTW6-Lyc3mHOUPoC1z_eekAgFp00NpcUOM-nrimfGD6I5S")
            .post(RequestBody.create(MediaType.parse("application/json"), payload))
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mensaje = "Ocurrió un error enviando la notificación"

                if (context != null) {
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("CambioEstado", mensaje)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val mensaje = "¡Cliente notificado!"

                    if (context != null) {
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("CambioEstado", mensaje)
                    }
                } else {
                    val mensaje = "No se pudo notificar, por favor reintente"

                    if (context != null) {
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("CambioEstado", mensaje)
                    }
                }
            }
        })
    }

    fun estaCancelado(): Boolean {
        return estado == ESTADO_CANCELADO
    }

    fun estaCompletado(): Boolean {
        return estado == ESTADO_COMPLETADO
    }

    companion object {
        const val ESTADO_PENDIENTE = "PENDIENTE"
        const val ESTADO_EN_PREPARACION = "EN_PREPARACION"
        const val ESTADO_COMPLETADO = "COMPLETADO"
        const val ESTADO_CANCELADO = "CANCELADO"

        private val STATES = mapOf(
            "todos" to "Todos",
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

        private fun parseProductos(productosCarrito: java.util.ArrayList<HashMap<String, Any>>): java.util.ArrayList<ProductoCarrito> {
            val productos: java.util.ArrayList<ProductoCarrito> = arrayListOf()

            for (producto in productosCarrito) {
                productos.add(ProductoCarrito(
                    Producto(
                        (producto.get("producto") as HashMap<String, Any>).get("id").toString(),
                        (producto.get("producto") as HashMap<String, Any>).get("nombre").toString(),
                        (producto.get("producto") as HashMap<String, Any>).get("observaciones").toString(),
                        (producto.get("producto") as HashMap<String, Any>).get("precio").toString().toLong(),
                        (producto.get("producto") as HashMap<String, Any>).get("stock").toString().toLong(),
                        (producto.get("producto") as HashMap<String, Any>).get("foto").toString(),
                        (producto.get("producto") as HashMap<String, Any>).get("categoria").toString(),
                        (producto.get("producto") as HashMap<String, Any>).get("excesoAzucar").toString().toBoolean(),
                        (producto.get("producto") as HashMap<String, Any>).get("excesoSodio").toString().toBoolean(),
                        (producto.get("producto") as HashMap<String, Any>).get("excesoGrasasSat").toString().toBoolean(),
                        (producto.get("producto") as HashMap<String, Any>).get("excesoGrasasTot").toString().toBoolean(),
                        (producto.get("producto") as HashMap<String, Any>).get("excesoCalorias").toString().toBoolean(),
                        (producto.get("producto") as HashMap<String, Any>).get("tienda").toString()
                    ),
                    producto.get("cantidad").toString().toLong()
                ))
            }

            return productos
        }
    }
}
