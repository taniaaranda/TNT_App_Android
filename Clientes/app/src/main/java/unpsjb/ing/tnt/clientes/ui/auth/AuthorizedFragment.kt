package unpsjb.ing.tnt.clientes.ui.auth

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.R

open class AuthorizedFragment : Fragment() {
    override fun onStart() {
        super.onStart()

        if (ClientesApplication.usuario == null) {
            (activity as HomeActivity).checkLogIn()
        }
    }
}