package unpsjb.ing.tnt.vendedores

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tnt.vendedores.databinding.FragmentMenuBinding


class CerrarSesionFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var menuView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = this.arguments?.getStringArrayList("email")
        var mail = view?.findViewById<TextView>(R.id.email)
        if (mail != null) {
            mail.text = email.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cerrar_sesion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button_cerrarSesion = view.findViewById<Button>(R.id.button_cerrarSesion)
        button_cerrarSesion.setOnClickListener {
            findNavController().navigate(R.id.loginvendedorFragment)
        }
        val button_iramenuvendedor = view.findViewById<Button>(R.id.button_iramenuvendedor)
        button_iramenuvendedor.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
    }


}