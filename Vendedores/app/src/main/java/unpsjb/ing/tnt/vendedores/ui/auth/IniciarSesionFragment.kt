package unpsjb.ing.tnt.vendedores.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.UnauthorizedActivity
import unpsjb.ing.tnt.vendedores.databinding.FragmentLoginBinding

class IniciarSesionFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )

        loginView = binding.root

        setBotones()

        return loginView
    }

    private fun setBotones() {
        binding.iniciarSesion.setOnClickListener {
            val email = binding.email.text
            val clave = binding.clave.text

            if (email.isNotEmpty() && clave.isNotEmpty()) {
                (activity as UnauthorizedActivity).logIn(email.toString(), clave.toString())
            } else {
                Toast.makeText(context, "Verifique los datos ingresados!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registro.setOnClickListener {
            findNavController().navigate(R.id.registroVendedorFragment)
        }
    }
}