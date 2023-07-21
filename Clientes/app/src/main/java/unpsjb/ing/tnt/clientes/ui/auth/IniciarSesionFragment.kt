package unpsjb.ing.tnt.clientes.ui.auth

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.UnauthorizedActivity

class IniciarSesionFragment : Fragment() {
    private lateinit var emailView: EditText
    private lateinit var claveView: EditText
    private lateinit var olvideClaveView: TextView
    private lateinit var botonIniciarSesion: Button
    private lateinit var botonRegistrarse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadViews(view)
        setListeners()
    }

    private fun loadViews(view: View) {
        emailView = view.findViewById(R.id.email)
        claveView = view.findViewById(R.id.clave)
        olvideClaveView = view.findViewById(R.id.olvide_clave)
        botonIniciarSesion = view.findViewById(R.id.boton_iniciar_sesion)
        botonRegistrarse = view.findViewById(R.id.boton_registro)
    }

    private fun setListeners() {
        botonIniciarSesion.setOnClickListener {
            val email = emailView.text.toString()
            val clave = claveView.text.toString()

            if (email.isNotEmpty() && clave.isNotEmpty()) {
                (activity as UnauthorizedActivity).logIn(email, clave)
            } else {
                Toast.makeText(context, "Debes ingresar email y clave", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        olvideClaveView.setOnClickListener {
            findNavController().navigate(R.id.reset_fragment)
        }

        botonRegistrarse.setOnClickListener {
            findNavController().navigate(R.id.register_fragment)
        }
    }
}