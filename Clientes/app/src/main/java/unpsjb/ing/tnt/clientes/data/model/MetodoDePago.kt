package unpsjb.ing.tnt.clientes.data.model

import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import unpsjb.ing.tnt.clientes.ClientesApplication
import java.io.IOException
import java.math.RoundingMode
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

class MetodoDePago(
    var tipo: String,
    var datos: HashMap<String, Any>
) {
    fun esValido(): Boolean {
        return if (tipo == TIPO_EFECTIVO) {
            validaEfectivo()
        } else {
            validaTarjeta()
        }
    }

    fun obtenerTarjetaOfuscada(): String {
        if (datos.containsKey("tarjeta")) {
            val tarjeta = datos["tarjeta"].toString().filterNot { it.isWhitespace() }

            return tarjeta.substring(0, 6) + "XXXXXX" + tarjeta.substring(12, 16)
        }

        return ""
    }

    fun obtenerPagaCon(): String {
        if (datos.containsKey("pagaCon")) {
            return datos["pagaCon"].toString().toBigDecimal().toString()
        }

        return ""
    }

    fun obtenerCuotas(): List<HashMap<String, Double>> {
        if (tipo == TIPO_TARJETA && datos.containsKey("tipo")) {
            return if (datos["tipo"] == "credit") {
                listOf(
                    hashMapOf("numero" to 1.0, "cft" to 0.0),
                    hashMapOf("numero" to 3.0, "cft" to 29.94),
                    hashMapOf("numero" to 6.0, "cft" to 54.12),
                    hashMapOf("numero" to 9.0, "cft" to 79.01),
                    hashMapOf("numero" to 12.0, "cft" to 104.06)
                )
            } else {
                listOf(
                    hashMapOf("numero" to 1.0, "cft" to 0.0)
                )
            }
        }

        return listOf()
    }

    fun obtenerCuotasParseadas(): List<String> {
        return obtenerCuotas().map {
            it["numero"].toString().toDouble().toInt().toString() +
                    " cuota/s de " +
                    (
                            (ClientesApplication.pedido!!.total +
                                    (it["cft"].toString().toDouble() / 100 * ClientesApplication.pedido!!.total)) /
                                    it["numero"].toString().toDouble().toInt()
                            ).toBigDecimal().setScale(2, RoundingMode.FLOOR).toString() +
                    " (CFT: " + it["cft"].toString() + "%)"
        }
    }

    fun esCredito(): Boolean {
        return tipo == TIPO_TARJETA && datos.containsKey("tipo") && datos["tipo"] == "credit"
    }

    fun esDebito(): Boolean {
        return tipo == TIPO_TARJETA && datos.containsKey("tipo") && datos["tipo"] == "debit"
    }

    fun esEfectivo(): Boolean {
        return tipo == TIPO_EFECTIVO
    }

    fun chequearBin() = runBlocking {
        if (datos.containsKey("tarjeta") && datos["tarjeta"].toString().length >= 6) {
            val datosTarjeta = checkTarjeta(datos["tarjeta"].toString())

            if (datosTarjeta["red"] != "" && datosTarjeta["tipo"] != "") {
                datos["red"] = datosTarjeta["red"].toString()
                datos["tipo"] = datosTarjeta["tipo"].toString()
            }
        }
    }

    private fun validaEfectivo(): Boolean {
        if (!datos.containsKey("pagaCon")) {
            return false
        }

        return true
    }

    private fun validaTarjeta(): Boolean {
        if (!datos.containsKey("tarjeta") ||
            !datos.containsKey("nombre") ||
            !datos.containsKey("dni") ||
            !datos.containsKey("tipo") ||
            !datos.containsKey("red")
        ) {
            return false
        }

        return true
    }

    companion object {
        const val TIPO_TARJETA = "TARJETA"
        const val TIPO_EFECTIVO = "EFECTIVO"

        private val client = OkHttpClient()

        fun new(tipo: String = "", datos: HashMap<String, Any> = hashMapOf()): MetodoDePago {
            return MetodoDePago(tipo, datos)
        }

        fun checkTarjetaAsync(tarjeta: String, callback: (tipo: String, red: String) -> Unit) {
            Thread().run {
                val request = Request.Builder()
                    .url("https://lookup.binlist.net/" + tarjeta.substring(0, min(tarjeta.length, 6)))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { callback("", "") }
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val jsonResponse = JSONObject(response.body()!!.string())
                            callback(jsonResponse["type"].toString(), jsonResponse["scheme"].toString())
                        } else {
                            callback("", "")
                        }
                    }
                })
            }
        }

        suspend fun checkTarjeta(tarjeta: String): HashMap<String, String> = suspendCoroutine {
            result -> checkTarjetaAsync(tarjeta) { tipo, red ->
                result.resume(hashMapOf(
                    "red" to red,
                    "tipo" to tipo
                ))
            }
        }
    }
}