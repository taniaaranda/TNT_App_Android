package unpsjb.ing.tnt.clientes.data.model

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