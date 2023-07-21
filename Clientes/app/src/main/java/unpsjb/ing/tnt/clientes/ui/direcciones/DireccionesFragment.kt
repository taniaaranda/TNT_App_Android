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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.DireccionesAdapter
import unpsjb.ing.tnt.clientes.data.model.Direccion
import unpsjb.ing.tnt.clientes.databinding.FragmentDireccionesBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class DireccionesFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentDireccionesBinding
    private lateinit var direccionesView: View

    private var direcciones: ArrayList<Direccion> = arrayListOf()
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
        setDireccionesListener()
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
        recyclerView = direccionesView.findViewById(R.id.listado_direcciones)
        recyclerView.layoutManager = LinearLayoutManager(context)

        direccionesAdapter = DireccionesAdapter(requireContext(), direcciones)
        recyclerView.adapter = direccionesAdapter
    }

    private fun setDireccionesListener() {
        FirebaseFirestore.getInstance().collection("direcciones")
            .whereEqualTo("usuario", FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    for (document in value.documents) {
                        if (direcciones.find { it.id == document.id } == null) {
                            direcciones.add(
                                Direccion(
                                    document.id,
                                    document.get("calle").toString(),
                                    document.get("ubicacion") as HashMap<String, Double>,
                                    document.get("usuario").toString()
                                )
                            )
                        }
                    }

                    direccionesAdapter.notifyDataSetChanged()
                }
            }
    }
}