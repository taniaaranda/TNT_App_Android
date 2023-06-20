package unpsjb.ing.tnt.clientes.ui.tiendas

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.TiendasAdapter
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.databinding.FragmentListadoTiendasBinding
import unpsjb.ing.tnt.clientes.ui.utils.FirebaseConnectedFragment


private const val  TIENDAS_COLLECTION_NAME = "tiendas"

class ListadoTiendasFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentListadoTiendasBinding
    private lateinit var listView: View
    private lateinit var fragmentContext: Context
    private var tiendasSnapshotListener: ListenerRegistration? = null

    private var filtroRubro: String = "Todos"
    private var filtroCercanas: Boolean = false
    private var filtroNombre: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_listado_tiendas, container, false
        )
        fragmentContext = this.requireContext()
        listView = binding.root

        setFiltroRubros()
        setFiltroTiendas()
        setSwitchUbicaciones()
        registerTiendasSnapshotListener()

        return listView
    }

    private fun setFiltroRubros() {
        val rubrosAdapter = ArrayAdapter(this.requireContext(),
            android.R.layout.simple_spinner_item, Tienda.getRubrosValues().map { it.nombre })
        binding.filtroRubros.adapter = rubrosAdapter

        if (arguments != null) {
            filtroRubro = requireArguments().getString("rubro").toString()

            val position = rubrosAdapter.getPosition(filtroRubro)
            binding.filtroRubros.setSelection(position)
        }

        binding.filtroRubros.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(fragmentContext, "Debes seleccionar algún filtro", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filtroRubro = binding.filtroRubros.selectedItem.toString()
                registerTiendasSnapshotListener()
            }
        }
    }

    private fun setFiltroTiendas() {
        binding.filtroTienda.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtroNombre = if (s == "") {
                    null
                } else {
                    s.toString()
                }

                registerTiendasSnapshotListener()
            }
        })
    }

    private fun setSwitchUbicaciones() {
        binding.switchUbicacion.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            filtroCercanas = isChecked
            registerTiendasSnapshotListener()
        })
    }

    private fun registerTiendasSnapshotListener() {
        tiendasSnapshotListener?.remove()

        tiendasSnapshotListener = getDbReference().collection(TIENDAS_COLLECTION_NAME)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val adapter = TiendasAdapter(
                    this.requireContext(),
                    filterTiendas(snapshots),
                    callbackVerTienda = { verTienda(it) }
                )

                binding.listadoTiendas.adapter = adapter
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun filterTiendas(snapshots: QuerySnapshot?): List<Tienda> {
        val tiendas: ArrayList<Tienda> = arrayListOf()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                if (filtroNombre != null && !document.get("nombre").toString().contains(filtroNombre.toString())) {
                    continue
                }

                if (filtroRubro != "Todos" && document.get("rubro") != filtroRubro) {
                    continue
                }

                // Filtrar cercanos

                tiendas.add(Tienda(
                    document.id,
                    document.get("nombre").toString(),
                    document.get("rubro").toString(),
                    document.get("calle").toString(),
                    document.get("ubicacionLatLong") as HashMap<String, Double>,
                    document.get("horario_de_atencion") as HashMap<String, String>,
                    document.get("metodos_de_pago") as ArrayList<String>
                ))
            }
        }

        return tiendas.toList().sortedWith(compareBy { it.id })
    }

    private fun verTienda(tiendaId: String) {
        val bundle = bundleOf("tiendaId" to tiendaId)

        if (ClientesApplication.carrito != null) {
            if (ClientesApplication.carrito!!.tienda == tiendaId || ClientesApplication.carrito!!.estaVacio()) {
                listView.findNavController().navigate(R.id.nav_products, bundle)
            } else {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Vas a perder el carrito. Continuar?")
                    .setPositiveButton("Si",
                        DialogInterface.OnClickListener { dialog, id ->
                            listView.findNavController().navigate(R.id.nav_products, bundle)
                        })
                    .setNegativeButton("Permanecer Aquí",
                        DialogInterface.OnClickListener { dialog, id -> })
                builder.show()
            }
        } else {
            listView.findNavController().navigate(R.id.nav_products, bundle)
        }
    }
}