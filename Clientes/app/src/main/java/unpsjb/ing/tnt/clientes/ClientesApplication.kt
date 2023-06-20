package unpsjb.ing.tnt.clientes

import android.app.Application
import unpsjb.ing.tnt.clientes.data.model.Carrito

class ClientesApplication: Application() {
    companion object {
        var carrito: Carrito? = null
        var cargandoStock: Boolean = false
    }
}