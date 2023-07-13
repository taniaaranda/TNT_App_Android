package unpsjb.ing.tnt.clientes.ui.direcciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.DireccionesAdapter
import unpsjb.ing.tnt.clientes.data.model.Direccion
import unpsjb.ing.tnt.clientes.databinding.FragmentDireccionesBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class DireccionesFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentDireccionesBinding
    private lateinit var direccionesView: View

    private lateinit var direcciones: ArrayList<Direccion>
    private lateinit var recyclerView: RecyclerView
    private lateinit var direccionesAdapter: DireccionesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_direcciones, container, false
        )

        direccionesView = binding.root

        setRecyclerView()
        setNuevaDireccionListener()

        return direccionesView
    }

    private fun setNuevaDireccionListener() {
        binding.nuevaDireccion.setOnClickListener {
            direccionesView.findNavController().navigate(R.id.nav_address, bundleOf(
                "modo" to "nuevo"
            ))
        }
    }

    private fun setRecyclerView() {
        direcciones = ClientesApplication.usuario!!.direcciones

        recyclerView = direccionesView.findViewById(R.id.listado_direcciones)
        recyclerView.layoutManager = LinearLayoutManager(context)

        direccionesAdapter = DireccionesAdapter(requireContext(), direcciones)
        recyclerView.adapter = direccionesAdapter
    }
}