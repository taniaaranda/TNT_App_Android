package unpsjb.ing.tnt.vendedores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn_alta_tienda = view.findViewById<Button>(R.id.btn_alta_tienda)

        btn_alta_tienda.setOnClickListener {
            findNavController().navigate(R.id.altaTiendaFragment)
        }

        val btn_alta_productos = view.findViewById<Button>(R.id.btn_alta_productos)

        btn_alta_productos.setOnClickListener {
            findNavController().navigate(R.id.altaProductosFragment)
        }


        val btn_menu_productos = view.findViewById<Button>(R.id.btn_menu_productos)

        btn_menu_productos.setOnClickListener {
            //findNavController().navigate(R.id.altaProductosFragment)
            findNavController().navigate(R.id.action_menuFragment_to_productosFragment)
        }



        val btn_listado_pedidos = view.findViewById<Button>(R.id.btn_listado_pedidos)

        btn_listado_pedidos.setOnClickListener {
            findNavController().navigate(R.id.listadoPedidosFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
