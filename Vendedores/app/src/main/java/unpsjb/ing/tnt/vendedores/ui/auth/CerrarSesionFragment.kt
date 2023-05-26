package unpsjb.ing.tnt.vendedores.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import unpsjb.ing.tnt.vendedores.HomeActivity


class CerrarSesionFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as HomeActivity).logOut()
    }

}