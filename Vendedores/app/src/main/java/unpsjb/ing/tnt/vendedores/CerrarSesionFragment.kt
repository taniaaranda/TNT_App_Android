package unpsjb.ing.tnt.vendedores

import android.os.Bundle
import androidx.fragment.app.Fragment


class CerrarSesionFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as HomeActivity).logOut()
    }

}