package unpsjb.ing.tnt.vendedores

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrovendedorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrovendedorFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_registrovendedor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = view.findViewById<EditText>(R.id.email)
        val pass = view.findViewById<EditText>(R.id.pass)
        val button_registrocliente = view.findViewById<Button>(R.id.button_registrovendedor)
        button_registrocliente.setOnClickListener {
            if (email.text.isNotEmpty() || pass.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            AlertDialog.Builder(context).apply {
                                setTitle("¡El vendedor se ha creado con éxito!").show()
                                val bundle = bundleOf("email" to email.text.toString())
                                findNavController().navigate(R.id.altaTiendaFragment, bundle)
                            }

                        } else {
                            AlertDialog.Builder(context).apply {
                                setTitle("¡Debe ingresar un email con forma xxx@xxxx y una contrasela alfanumerica!").show()
                                findNavController().navigate(R.id.registrovendedorFragment)
                            }
                        }
                    }

            } else {
                if(email.text.isEmpty()){
                    email.error ="Debe ingresar un email con la forma xxxx@xxxx.com"
                }
                if(pass.text.isEmpty()){
                    pass.error ="Debe ingresar una contraseña"
                }
                AlertDialog.Builder(context).apply {
                    setTitle("¡Debe ingresar los datos requeridos!").show()
                }
                findNavController().navigate(R.id.registrovendedorFragment)
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
         * @return A new instance of fragment RegistrovendedorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrovendedorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}