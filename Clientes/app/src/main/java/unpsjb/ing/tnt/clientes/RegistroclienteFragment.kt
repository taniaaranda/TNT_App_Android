package unpsjb.ing.tnt.clientes

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistroclienteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistroclienteFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_registrocliente, container, false)
    }


    //VOLVER ACA !!
    //Todavia falta recuperar el usuario para enviarlo a los fragmentos que lo usan, no encuentro
    //como pasar este dato entre fragmentos aun, una vez iniciada la session deberia pasar al fragmento
    //cerrar sesion, la navegacion esta hecha

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = view.findViewById<EditText>(R.id.email)
        val pass = view.findViewById<EditText>(R.id.pass)
        val direccion = view.findViewById<EditText>(R.id.direccion)
        // val button_registro1 = view.findViewById<Button>(R.id.button_registro)
        val button_registro = view.findViewById<Button>(R.id.button_registrocliente)
        button_registro.setOnClickListener {
            if (email.text.isNotEmpty() || pass.text.isNotEmpty() || direccion.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                AlertDialog.Builder(context).apply {
                    setTitle("¡El cliente se ha creado con éxito!").show()
                    findNavController().navigate(R.id.homeFragment)

                }
            } else {
                if(email.text.isEmpty()){
                    email.error ="Debe ingresar un email con la forma xxxx@xxxx.com"
                }
                if(pass.text.isEmpty()){
                    pass.error ="Debe ingresar una contraseña"
                }
                if(direccion.text.isEmpty()){
                    direccion.error ="Debe ingresar una contraseña"
                }
                AlertDialog.Builder(context).apply {
                    setTitle("¡Debe ingresar los datos requeridos!").show()
                }
                findNavController().navigate(R.id.registroclienteFragment)
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
         * @return A new instance of fragment RegistroclienteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistroclienteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}