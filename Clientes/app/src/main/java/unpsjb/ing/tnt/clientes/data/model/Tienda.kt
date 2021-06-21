package unpsjb.ing.tnt.clientes.data.model

class Tienda(val id: String, val rubro: String, val ubicacion: String, val horario_de_atencion: ArrayList<String>) {

    fun getFormattedId(): String {
        return "Tienda #$id"
    }

    fun getFormattedHorario(): String {
        return "${horario_de_atencion.get(0) }hs"+" - "+"${horario_de_atencion.get(1)}hs"
    }
}