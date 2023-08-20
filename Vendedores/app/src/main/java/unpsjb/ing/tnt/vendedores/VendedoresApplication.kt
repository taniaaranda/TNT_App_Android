package unpsjb.ing.tnt.vendedores

import android.app.Application
import unpsjb.ing.tnt.vendedores.data.model.Tienda

class VendedoresApplication: Application() {
    companion object {
        var tienda: Tienda? = null
    }
}