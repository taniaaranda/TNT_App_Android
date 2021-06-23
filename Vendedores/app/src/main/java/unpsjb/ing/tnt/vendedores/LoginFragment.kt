package unpsjb.ing.tnt.vendedores

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

//volver aca, falta controlar que el usuario exista en la base de datos, que no ingresa vacio, la aplicacion
    //falla, por eso comentado
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // val email = view.findViewById<EditText>(R.id.email)
        //val pass = view.findViewById<EditText>(R.id.pass)
       // if (email.text.isNotEmpty() || pass.text.isNotEmpty()) {
          //  val button_registrar = view.findViewById<Button>(R.id.button_registrar)
        val button_iniciar_sesion = view.findViewById<Button>(R.id.button_iniciar_sesion)
            button_iniciar_sesion.setOnClickListener {
               // FirebaseAuth.getInstance().singOut()
                findNavController().navigate(R.id.menuFragment)
            }
        //}
        /**} else {
            if (email.text.isEmpty()) {
                email.error = "Debe ingresar un email con la forma xxxx@xxxx.com"
            }
            if (pass.text.isEmpty()) {
                pass.error = "Debe ingresar una contraseña"
            }
            AlertDialog.Builder(context).apply {
                setTitle("¡Debe ingresar los datos requeridos!").show()
            }**/
            val button_registrar = view.findViewById<Button>(R.id.button_registrar)
            button_registrar.setOnClickListener {
                findNavController().navigate(R.id.registroFragment)
            }
        }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}