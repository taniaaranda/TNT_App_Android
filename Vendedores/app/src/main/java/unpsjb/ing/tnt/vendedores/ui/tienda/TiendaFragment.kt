package unpsjb.ing.tnt.vendedores.ui.tienda

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.MapView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.databinding.FragmentTiendaBinding
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment

class TiendaFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentTiendaBinding
    private lateinit var listView: View
    private lateinit var fragmentContext: Context

    private lateinit var nombreView: TextView
    private lateinit var rubroView: TextView
    private lateinit var direccionView: TextView
    private lateinit var mapaView: MapView
    private lateinit var horarioAperturaView: TextView
    private lateinit var horarioCierreView: TextView
    private lateinit var efectivoView: CheckBox
    private lateinit var debitoView: CheckBox
    private lateinit var creditoView: CheckBox
    private lateinit var editarButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_tienda, container, false
        )

        fragmentContext = this.requireContext()
        listView = binding.root

        setViews()
        getTienda()

        return listView
    }

    private fun setViews() {
        nombreView = listView.findViewById(R.id.nombre_tienda);
        rubroView = listView.findViewById(R.id.rubro);
        direccionView = listView.findViewById(R.id.direccion);
        mapaView = listView.findViewById(R.id.mapa);
        horarioAperturaView = listView.findViewById(R.id.horario_apertura);
        horarioCierreView = listView.findViewById(R.id.horario_cierre);
        efectivoView = listView.findViewById(R.id.efectivo);
        debitoView = listView.findViewById(R.id.debito);
        creditoView = listView.findViewById(R.id.credito);
        //editarButton = listView.findViewById(R.id.editar);
    }

    private fun getTienda() {
        getDbReference().collection("tiendas")
            .whereEqualTo("usuario", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                hidratarFormulario(it.documents.first())
            }
            .addOnFailureListener {
                Toast.makeText(context, "No se pudo cargar la información de la tienda", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hidratarFormulario(tienda: DocumentSnapshot) {
        val horarios = tienda.get("horario_de_atencion") as HashMap<*, *>
        val metodosDePago = tienda.get("metodos_de_pago") as ArrayList<*>
        Log.d("TiendaFragment", metodosDePago.toString())

        nombreView.text = tienda.get("nombre") as String
        rubroView.text = tienda.get("rubro") as String
        direccionView.text = tienda.get("calle") as String
        horarioAperturaView.text = horarios.get("apertura") as String
        horarioCierreView.text = horarios.get("cierre") as String
        efectivoView.isChecked = metodosDePago.contains("Efectivo")
        debitoView.isChecked = metodosDePago.contains("Debito")
        creditoView.isChecked = metodosDePago.contains("Credito")
    }
}