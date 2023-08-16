package unpsjb.ing.tnt.clientes.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId

class Tienda(
    val id: String,
    val nombre: String,
    val rubro: String,
    val calle: String,
    val ubicacion: HashMap<String, Double>,
    val horarioDeAtencion: HashMap<String, String>,
    val metodosDePago: ArrayList<String>
) {
    fun getFormattedHorario(): String {
        return "${horarioDeAtencion["apertura"]}hs"+" - "+"${horarioDeAtencion["cierre"]}hs"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun estaAbierto(): Boolean {
        val fechaHoraActual = Instant.now().atZone(ZoneId.systemDefault())
        val horarioApertura = Instant.parse(
            fechaHoraActual.toLocalDate().toString() + "T" + horarioDeAtencion["apertura"] + ":00.000Z"
        ).atZone(ZoneId.systemDefault()).toLocalTime()
        val horarioCierre = Instant.parse(
            fechaHoraActual.toLocalDate().toString() + "T" + horarioDeAtencion["cierre"] + ":00.000Z"
        ).atZone(ZoneId.systemDefault()).toLocalTime()

        return fechaHoraActual.toLocalTime() in horarioApertura..horarioCierre
    }

    companion object {
        private val RUBROS = arrayListOf(
            Rubro("Todos"),
            Rubro("Despensa"),
            Rubro("Ferretería"),
            Rubro("Mercería"),
            Rubro("Heladería"),
            Rubro("Rotisería")
        )

        fun getRubrosValues(): ArrayList<Rubro> {
            return RUBROS
        }
    }
}