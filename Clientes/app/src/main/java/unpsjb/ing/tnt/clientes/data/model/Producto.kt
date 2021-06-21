package unpsjb.ing.tnt.clientes.data.model

import android.graphics.Bitmap

class Producto(
    val id: String,
    val nombre: String,
    val cantidadDisponible: Long,
    val precioUnitario: Long,
    val categoria: String,
    val observacones: String
)