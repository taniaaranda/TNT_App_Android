package unpsjb.ing.tnt.clientes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.clientes.adapter.TiendasAdapter
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.databinding.FragmentListadoTiendasBinding

private const val  TIENDAS_COLLECTION_NAME = "tiendas"

class ListadoTiendasFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentListadoTiendasBinding
    private lateinit var listView: View

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
        listView = binding.root

        getDbReference().collection(TIENDAS_COLLECTION_NAME)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("ListadoTiendas", e.message.toString())
                        return@addSnapshotListener
                    }

                    Log.d("ListadoTiendas", parseTiendas(snapshots).size.toString())

                    val adapter = TiendasAdapter(this.requireContext(), parseTiendas(snapshots))
                    binding.listadoTiendas.adapter = adapter
                }

        return listView
    }

    private fun parseTiendas(snapshots: QuerySnapshot?): List<Tienda> {
        val tiendas = ArrayList<Tienda>()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                tiendas.add(
                        Tienda(
                                document.get("id") as String,
                                document.get("rubro") as String,
                                document.get("ubicacion") as String,
                                document.get("horario_de_atencion") as ArrayList<String>,
                                document.get("metodos_de_pago") as ArrayList<String>
                        )
                )
            }
        }

        return tiendas.sortedWith(compareBy { it.id })
    }
}