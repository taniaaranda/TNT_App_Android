package unpsjb.ing.tnt.vendedores

import android.app.AlertDialog
import android.os.Bundle
import android.renderscript.ScriptGroup
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginvendedorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginvendedorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loginvendedor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val email = view.findViewById<EditText>(R.id.email)
        val contraseña = view.findViewById<EditText>(R.id.contraseña)
        val button_vendedorregistro = view.findViewById<Button>(R.id.button_vendedorregistro)
        button_vendedorregistro.setOnClickListener {
            findNavController().navigate(R.id.registrovendedorFragment)
        }
        val button_iniciar_sesionvendedor = view.findViewById<Button>(R.id.button_iniciar_sesionvendedor)
        button_iniciar_sesionvendedor.setOnClickListener {
            if (email.text.isNotEmpty() || contraseña.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.text.toString(), contraseña.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            AlertDialog.Builder(context).apply {
                                setTitle("¡Login exitoso!").show()
                                val bundle = bundleOf("email" to email.text.toString())
                                findNavController().navigate(R.id.menuFragment, bundle)
                            }

                        } else {
                            AlertDialog.Builder(context).apply {
                                setTitle("¡Usuario o contraeña incorrectos!").show()
                                findNavController().navigate(R.id.loginvendedorFragment)
                            }
                        }
                    }
            } else {
                AlertDialog.Builder(context).apply {
                    setTitle("¡Ingrese los datos!").show()
                    findNavController().navigate(R.id.loginvendedorFragment)
                }
            }
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginvendedorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginvendedorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}