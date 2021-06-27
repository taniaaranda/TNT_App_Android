package unpsjb.ing.tnt.vendedores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class MenuFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            findNavController().navigate(R.id.listadoProductosFragment)
        }

        val btn_listado_pedidos = view.findViewById<Button>(R.id.btn_listado_pedidos)
        btn_listado_pedidos.setOnClickListener {
            findNavController().navigate(R.id.listadoPedidosFragment)
        }

        val btn_cerrar_sesion = view.findViewById<Button>(R.id.cerrar_sesion)
        btn_cerrar_sesion.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_loginFragment3)
        }
    }
}