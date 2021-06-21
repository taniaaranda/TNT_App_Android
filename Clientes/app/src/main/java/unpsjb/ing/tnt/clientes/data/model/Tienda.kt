package unpsjb.ing.tnt.clientes.data.model

class Tienda(val id: String, val rubro: String, val ubicacion: String, val horario_de_atencion: ArrayList<String>,val metodos_de_pago: ArrayList<String>) {

    fun getFormattedId(): String {
        return "Tienda #$id"
    }

}