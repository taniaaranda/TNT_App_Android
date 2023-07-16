package unpsjb.ing.tnt.clientes.data.model

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
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

    fun getTarjetaOfuscada(): String {
        if (datos.containsKey("tarjeta")) {
            val tarjeta = datos["tarjeta"].toString().filterNot { it.isWhitespace() }

            return tarjeta.substring(0, 6) + "XXXXXX" + tarjeta.substring(12, 16)
        }

        return ""
    }

    fun getPagaCon(): String {
        if (datos.containsKey("pagaCon")) {
            return datos["pagaCon"].toString().toBigDecimal().toString()
        }

        return ""
    }

    fun getCuotas(): List<HashMap<String, Double>> {
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

        fun checkTarjeta(tarjeta: String, callbackSuccess: (tipo: String, red: String) -> Unit, callbackError: () -> Unit) {
            val request = Request.Builder()
                .url("https://lookup.binlist.net/" + tarjeta.substring(0, min(tarjeta.length, 6)))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { callbackError() }
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(response.body()!!.string())
                        callbackSuccess(jsonResponse["type"].toString(), jsonResponse["scheme"].toString())
                    } else {
                        callbackError()
                    }
                }
            })
        }
    }
}