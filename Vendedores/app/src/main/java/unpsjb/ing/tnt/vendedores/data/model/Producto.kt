package unpsjb.ing.tnt.vendedores.data.model

import android.graphics.Bitmap

class Producto (
    val id: Int,
    val nombre: String,
    val cantidadDisponible: Int,
    val precioUnitario: Float,
    val categoría: String,
    val fotografía: Bitmap,
    val observaciones: String
)