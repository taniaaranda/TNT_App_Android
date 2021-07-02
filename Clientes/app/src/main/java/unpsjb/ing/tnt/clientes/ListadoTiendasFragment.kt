package unpsjb.ing.tnt.clientes

import android.content.ContentValues
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
import android.widget.Switch
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

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
        val usuario = arguments?.getString("usuario")
        prepareSpinner(usuario.toString())
        prepareEditText(usuario.toString())
        prepareSwitch(usuario.toString())
        registerTiendasSnapshotListener(usuario.toString())

        return listView
    }

    private fun prepareSpinner(usuario: String) {
        val rubrosAdapter = ArrayAdapter(this.requireContext(),
                android.R.layout.simple_spinner_item, Tienda.getRubrosValues())
        binding.filtroRubros.adapter = rubrosAdapter

        binding.filtroRubros.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(fragmentContext, "Debes seleccionar algún filtro", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                registerTiendasSnapshotListener(usuario)
            }
        }
    }

    private fun prepareEditText(usuario: String) {
        binding.filtroNombre.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                registerTiendasSnapshotListener(usuario)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun prepareSwitch(usuario: String) {
        binding.switchUbicacion.setOnCheckedChangeListener { Switch, isChecked ->
            if (isChecked) {
                Log.i(ContentValues.TAG, "FUNCIONA EL LISTENER")
                registerTiendasSnapshotListener(usuario)
            }
        }
    }

    private fun registerTiendasSnapshotListener(usuario:String) {
        tiendasSnapshotListener?.remove()

        var selectedFilter = binding.filtroRubros.selectedItem
        var txtNombreFilter = binding.filtroNombre.text.toString()
        var switchUbicacionFilter = binding.switchUbicacion.isChecked
        var ubicacionUsuario: ArrayList<Double> = arrayListOf(0.0,0.0)
        getDbReference().collection("datosClientes").whereEqualTo("usuario",usuario).get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    ubicacionUsuario = queryDocumentSnapshots.documents.get(0).get("ubicacionLatLong") as ArrayList<Double>
                }

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

                    val adapter = TiendasAdapter(this.requireContext(), parseTiendas(snapshots, selectedFilter as String, txtNombreFilter, switchUbicacionFilter,ubicacionUsuario))
                    binding.listadoTiendas.adapter = adapter
                }
    }

    private fun parseTiendas(snapshots: QuerySnapshot?, filtro: String, filtroNombre: String, filtroUbicacion: Boolean, ubicacionUsuario: ArrayList<Double>): List<Tienda> {
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

                if(filtroUbicacion){
                    Log.i(ContentValues.TAG, "is checked")
                    val ubicacionTienda = document.get("ubicacionLatLong") as  ArrayList<Double>
                    val url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+ubicacionTienda.get(0).toString()+","+ubicacionTienda.get(1).toString()+
                            "&destinations="+ubicacionUsuario.get(0).toString()+","+ubicacionUsuario.get(1).toString()+"&key="+getString(R.string.android_sdk_places_apt_key)

                    val jsonObjectRequest = JsonObjectRequest(
                        Request.Method.GET, url, null,
                        { response ->
                            val rows = response.getJSONArray("rows")
                            val elements = rows.getJSONObject(0).getJSONArray("elements")
                            val distance = elements.getJSONObject(0).getJSONObject("distance").getString("value")
                            Log.i(ContentValues.TAG, "DISTANCE"+distance)
                        },
                        { error ->
                            // TODO: Handle error
                            Log.e(ContentValues.TAG,"errooooor")
                        }
                    )

                    Log.i(ContentValues.TAG,"PASOO")

                    // Access the RequestQueue through your singleton class.
                    //MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
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