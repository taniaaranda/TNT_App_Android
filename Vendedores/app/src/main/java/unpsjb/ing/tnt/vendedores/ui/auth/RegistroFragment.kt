package unpsjb.ing.tnt.vendedores.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import unpsjb.ing.tnt.vendedores.NuevaTiendaActivity
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.UnauthorizedActivity
import unpsjb.ing.tnt.vendedores.databinding.FragmentRegistroBinding

class RegistroFragment : Fragment() {
    private lateinit var binding: FragmentRegistroBinding
    private lateinit var registroView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_registro, container, false
        )

        registroView = binding.root

        setBotones()

        return registroView
    }

    private fun setBotones() {
        binding.registrarse.setOnClickListener {
            val email = binding.email.text
            val clave = binding.clave.text

            if (email.isEmpty() || clave.isEmpty()) {
                Toast.makeText(context, "Verifique los datos ingresados", Toast.LENGTH_SHORT)
                    .show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    email.toString(), clave.toString()
                ).addOnSuccessListener {
                    Toast.makeText(context, "Registración exitosa!", Toast.LENGTH_SHORT).show()

                    (activity as UnauthorizedActivity).finish()
                    startActivity(Intent(requireContext(), NuevaTiendaActivity::class.java))
                }.addOnFailureListener {
                    Toast.makeText(context, "Ocurrió un error, por favor reintente", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.volver.setOnClickListener {
            findNavController().navigate(R.id.loginVendedorFragment)
        }
    }
}