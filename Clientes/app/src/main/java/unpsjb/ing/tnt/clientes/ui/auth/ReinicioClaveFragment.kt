package unpsjb.ing.tnt.clientes.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import unpsjb.ing.tnt.clientes.R

class ReinicioClaveFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reinicio_clave, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.boton_resetear_clave).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.email).text.toString()

            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(context, "¡Email enviado!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.login_fragment)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "No se pudo enviar el reset, pro favor vuelva a intentarlo", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Debes indicar un email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}