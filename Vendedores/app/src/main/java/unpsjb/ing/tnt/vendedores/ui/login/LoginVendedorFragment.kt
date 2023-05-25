package unpsjb.ing.tnt.vendedores.ui.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.UnauthorizedActivity

class LoginVendedorFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = view.findViewById<EditText>(R.id.email)
        val clave = view.findViewById<EditText>(R.id.contraseña)

        val registroButton = view.findViewById<Button>(R.id.button_vendedorregistro)
        registroButton.setOnClickListener {
            findNavController().navigate(R.id.registroVendedorFragment)
        }

        val loginButton = view.findViewById<Button>(R.id.button_iniciar_sesionvendedor)
        loginButton.setOnClickListener {
            if (email.text.isNotEmpty() && clave.text.isNotEmpty()) {
                (activity as UnauthorizedActivity).logIn(email.text.toString(), clave.text.toString())
            } else {
                AlertDialog.Builder(context).apply{
                    setTitle("¡Ingrese los datos!")
                    setPositiveButton("Aceptar"){ _: DialogInterface, _: Int ->
                        findNavController().navigate(R.id.loginVendedorFragment)
                    }
                }.show()
            }
        }
    }
}