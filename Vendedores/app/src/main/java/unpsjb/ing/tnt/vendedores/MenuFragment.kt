package unpsjb.ing.tnt.vendedores

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.vendedores.databinding.FragmentMenuBinding

class MenuFragment : FirebaseConnectedFragment() {
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
        val email = arguments?.getString("email")
        getDbReference().collection("tiendas").whereEqualTo("usuario",email.toString()).get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    setUpBindings(email.toString(), queryDocumentSnapshots.documents.get(0).id)
                }

    }

    private fun setUpBindings(usuario: String, tienda: String) {

        val bundle = bundleOf("tienda" to tienda)
        val bundle_usuario = bundleOf("usuario" to usuario)

        binding.btnAltaProductos.setOnClickListener {
            findNavController().navigate(R.id.altaProductosFragment, bundle)
        }

        binding.btnMenuProductos.setOnClickListener {
            findNavController().navigate(R.id.listadoProductosFragment, bundle)
        }

        binding.btnListadoPedidos.setOnClickListener {
            findNavController().navigate(R.id.listadoPedidosFragment, bundle)
        }

        binding.cerrarSesion.setOnClickListener {
            findNavController().navigate(R.id.cerrarSesionFragment, bundle_usuario)
        }

    }
}