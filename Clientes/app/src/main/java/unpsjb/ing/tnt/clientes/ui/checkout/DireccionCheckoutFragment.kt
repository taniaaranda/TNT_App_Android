package unpsjb.ing.tnt.clientes.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class DireccionCheckoutFragment : AuthorizedFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_direccion_checkout, container, false)
    }
}