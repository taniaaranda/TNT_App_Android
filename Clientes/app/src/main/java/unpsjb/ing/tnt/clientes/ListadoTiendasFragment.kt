package unpsjb.ing.tnt.clientes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.clientes.adapter.TiendasAdapter
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.databinding.FragmentListadoTiendasBinding
import com.google.firebase.firestore.ListenerRegistration
import android.content.Context
import android.text.Editable
import android.text.TextWatcher

private const val  TIENDAS_COLLECTION_NAME = "tiendas"

class ListadoTiendasFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentListadoTiendasBinding
    private lateinit var listView: View
    private lateinit var fragmentContext: Context
    private var tiendasSnapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_listado_tiendas, container, false
        )
        fragmentContext = this.requireContext()
        listView = binding.root

        prepareSpinner()
        prepareEditText()
        registerTiendasSnapshotListener()

        return listView
    }

    private fun prepareSpinner() {
        val rubrosAdapter = ArrayAdapter(this.requireContext(),
                android.R.layout.simple_spinner_item, Tienda.getRubrosValues())
        binding.filtroRubros.adapter = rubrosAdapter

        binding.filtroRubros.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(fragmentContext, "Debes seleccionar algún filtro", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                registerTiendasSnapshotListener()
            }
        }
    }

    private fun prepareEditText() {
        binding.filtroNombre.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                registerTiendasSnapshotListener()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun registerTiendasSnapshotListener() {
        tiendasSnapshotListener?.remove()

        var selectedFilter = binding.filtroRubros.selectedItem
        var txtNombreFilter = binding.filtroNombre.text.toString()

        if (selectedFilter == null) {
            binding.filtroRubros.setSelection(0)
            selectedFilter = binding.filtroRubros.selectedItem
        }

        tiendasSnapshotListener = getDbReference().collection(TIENDAS_COLLECTION_NAME)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("ListadoTiendas", e.message.toString())
                        return@addSnapshotListener
                    }

                    val adapter = TiendasAdapter(this.requireContext(), parseTiendas(snapshots, selectedFilter as String, txtNombreFilter as String))
                    binding.listadoTiendas.adapter = adapter
                }
    }

    private fun parseTiendas(snapshots: QuerySnapshot?, filtro: String, filtroNombre: String): List<Tienda> {
        val tiendas = ArrayList<Tienda>()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                val rubro = document.get("rubro") as String
                val nombre = document.id.toString()

                if (filtro != "Todos" && rubro != filtro) {
                    continue
                }

                if (filtroNombre != "" && !filtroNombre.equals(nombre, true) && !nombre.startsWith(filtroNombre,true)){
                    continue
                }

                tiendas.add(
                        Tienda(
                                document.id ,
                                document.get("rubro") as String,
                                document.get("ubicacion") as String,
                                document.get("horario_de_atencion") as ArrayList<String>,
                                //document.get("metodos_de_pago") as ArrayList<String>
                        )
                )
            }
        }

        return tiendas.sortedWith(compareBy { it.id })
    }
}