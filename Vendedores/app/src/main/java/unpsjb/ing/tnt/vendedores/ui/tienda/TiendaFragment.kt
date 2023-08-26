package unpsjb.ing.tnt.vendedores.ui.tienda

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainerView
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.data.model.Tienda
import unpsjb.ing.tnt.vendedores.databinding.FragmentTiendaBinding
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment

class TiendaFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentTiendaBinding
    private lateinit var listView: View
    private lateinit var fragmentContext: Context

    private lateinit var tienda: Tienda
    private lateinit var nuevaDireccion: String
    private lateinit var nuevaUbicacion: HashMap<String, Double>

    private lateinit var nombreView: TextView
    private lateinit var rubroView: AppCompatSpinner
    private lateinit var direccionView: TextView
    private lateinit var ubicacionView: AutocompleteSupportFragment
    private lateinit var horarioAperturaView: AppCompatSpinner
    private lateinit var horarioCierreView: AppCompatSpinner
    private lateinit var efectivoView: CheckBox
    private lateinit var debitoView: CheckBox
    private lateinit var creditoView: CheckBox

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
        setUbicacionListener()
        getTienda()
        setBotonGuardar()

        return listView
    }

    private fun setViews() {
        nombreView = listView.findViewById(R.id.nombre_tienda)
        rubroView = listView.findViewById(R.id.rubro)
        ubicacionView = childFragmentManager.findFragmentById(R.id.ubicacion) as AutocompleteSupportFragment
        direccionView = listView.findViewById(R.id.direccion)
        horarioAperturaView = listView.findViewById(R.id.horarios_apertura_list)
        horarioCierreView = listView.findViewById(R.id.horarios_cierre_list)
        efectivoView = listView.findViewById(R.id.check_box_efectivo)
        debitoView = listView.findViewById(R.id.check_box_debito)
        creditoView = listView.findViewById(R.id.check_box_credito)
    }

    private fun setUbicacionListener() {
        ubicacionView.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                nuevaDireccion = place.name.toString()

                val ubicacionElegida = arrayListOf(place.latLng!!.latitude, place.latLng!!.longitude)
                nuevaUbicacion = hashMapOf("lat" to ubicacionElegida[0], "lng" to ubicacionElegida[1])
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })
    }

    private fun getTienda() {
        getDbReference().collection("tiendas")
            .whereEqualTo("usuario", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    val document = it.documents.first()

                    tienda = Tienda(
                        document.id,
                        document.get("nombre").toString(),
                        document.get("rubro").toString(),
                        document.get("calle").toString(),
                        document.get("ubicacionLatLong") as HashMap<String, Double>,
                        document.get("horario_de_atencion") as HashMap<String, String>,
                        document.get("metodos_de_pago") as ArrayList<String>
                    )

                    hidratarFormulario()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "No se pudo cargar la información de la tienda", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hidratarFormulario() {
        nombreView.text = tienda.nombre
        efectivoView.isChecked = tienda.metodosDePago.contains("Efectivo")
        debitoView.isChecked = tienda.metodosDePago.contains("Débito")
        creditoView.isChecked = tienda.metodosDePago.contains("Crédito")

        hidratarDireccion()
        hidratarRubros()
        hidratarHorarios()
    }

    private fun hidratarDireccion() {
        Places.initialize(
            requireContext().applicationContext,
            getString(R.string.android_sdk_places_apt_key)
        )
        ubicacionView.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))

        direccionView.text = tienda.calle
    }

    private fun hidratarRubros() {
        val rubros = arrayListOf("Despensa", "Ferretería", "Mercería","Heladería", "Rotisería")

        rubroView.adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            rubros
        )

        rubroView.setSelection(rubros.indexOf(tienda.rubro))
    }

    private fun hidratarHorarios() {
        val horarios = ArrayList<String>()
        horarios.add("Elegir horario")
        for (hora in 0..23) {
            for (minuto in 0..59 step 15) {
                horarios.add(hora.toString().padStart(2, '0') + ":" + minuto.toString().padStart(2, '0'))
            }
        }
        val horarioAperturaAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, horarios
        )

        horarioAperturaView.adapter = horarioAperturaAdapter
        horarioAperturaView.setSelection(horarios.indexOf(tienda.horarioDeAtencion["apertura"]))

        val horarioCierreAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, horarios
        )

        horarioCierreView.adapter = horarioCierreAdapter
        horarioCierreView.setSelection(horarios.indexOf(tienda.horarioDeAtencion["cierre"]))
    }

    private fun setBotonGuardar() {
        listView.findViewById<Button>(R.id.guardar).setOnClickListener {
            if (formValido()) {
                val tiendaToSave = getFormData()

                FirebaseFirestore.getInstance().collection("tiendas")
                    .document(tienda.id)
                    .update(getFormData())
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "¡Guardado!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "No se pudo guardar", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun formValido(): Boolean {
        val camposInvalidos = arrayListOf<String>()

        if (nombreView.text.isEmpty()) {
            camposInvalidos.add("Nombre")
        }

        if (rubroView.selectedItem.toString().isEmpty()) {
            camposInvalidos.add("Rubro")
        }

        if ((tienda.calle.isEmpty() && nuevaDireccion.isEmpty()) || (tienda.ubicacion.isEmpty() && nuevaUbicacion.isEmpty())) {
            camposInvalidos.add("Direccion")
        }

        if (horarioAperturaView.selectedItem.toString().isEmpty()) {
            camposInvalidos.add("Horario de Apertura")
        }

        if (horarioCierreView.selectedItem.toString().isEmpty()) {
            camposInvalidos.add("Horario de Cierre")
        }

        if (
            !efectivoView.isChecked &&
            !debitoView.isChecked &&
            !creditoView.isChecked
        ) {
            camposInvalidos.add("Metodos de Pago")
        }

        if (camposInvalidos.isNotEmpty()) {
            Toast.makeText(requireContext(), "Los siguientes campos son invalidos: " + camposInvalidos.joinToString(", "), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun getFormData(): HashMap<String, Any> {
        var metodosDePago = arrayListOf<String>()
        if (efectivoView.isChecked) {
            metodosDePago.add("Efectivo")
        }

        if (debitoView.isChecked) {
            metodosDePago.add("Débito")
        }

        if (creditoView.isChecked) {
            metodosDePago.add("Crédito")
        }

        return hashMapOf(
            "nombre" to nombreView.text.toString(),
            "rubro" to rubroView.selectedItem.toString(),
            "calle" to if (this::nuevaDireccion.isInitialized) {
                nuevaDireccion
            } else {
                tienda.calle
            },
            "ubicacionLatLong" to if (this::nuevaUbicacion.isInitialized) {
                nuevaUbicacion
            } else {
                tienda.ubicacion
            },
            "horario_de_atencion" to hashMapOf(
                "apertura" to horarioAperturaView.selectedItem.toString(),
                "cierre" to horarioCierreView.selectedItem.toString()
            ),
            "metodos_de_pago" to metodosDePago.toList()
        )
    }
}