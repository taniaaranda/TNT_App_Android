package unpsjb.ing.tnt.vendedores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tnt.vendedores.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var menuView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_menu, container, false
        )
        menuView = binding.root

        return menuView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBindings()
    }

    private fun setUpBindings() {
        binding.btnAltaTienda.setOnClickListener {
            findNavController().navigate(R.id.altaTiendaFragment)
        }

        binding.btnAltaProductos.setOnClickListener {
            findNavController().navigate(R.id.altaProductosFragment)
        }

        binding.btnMenuProductos.setOnClickListener {
            findNavController().navigate(R.id.listadoProductosFragment)
        }

        binding.btnListadoPedidos.setOnClickListener {
            findNavController().navigate(R.id.listadoPedidosFragment)
        }

        binding.cerrarSesion.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_loginFragment3)
        }
    }
}