package unpsjb.ing.tnt.clientes.data.model

val RUBROS = arrayOf("Todos","Despensa", "Ferretería","Mercería","Heladería","Rotisería")

class Tienda(val id: String, val rubro: String, val ubicacion: String, val horario_de_atencion: ArrayList<String>) {

    fun getFormattedHorario(): String {
        return "${horario_de_atencion.get(0) }hs"+" - "+"${horario_de_atencion.get(1)}hs"
    }

    companion object {

        fun getRubrosValues(): Array<String> {
            return RUBROS
        }

    }

}