package unpsjb.ing.tnt.vendedores.data.model

class MetodoDePago(
    var tipo: String,
    var datos: HashMap<String, Any>
) {
    companion object {
        const val TIPO_TARJETA = "TARJETA"
        const val TIPO_EFECTIVO = "EFECTIVO"
    }
}