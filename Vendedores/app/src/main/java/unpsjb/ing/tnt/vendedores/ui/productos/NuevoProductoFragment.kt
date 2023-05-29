package unpsjb.ing.tnt.vendedores.ui.productos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.databinding.FragmentNuevoProductoBinding

class NuevoProductoFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentNuevoProductoBinding
    private lateinit var altaProductosView: View
    private var categorias: ArrayList<String> =
        arrayListOf("Categoria 1", "Categoria 2", "Categoria 3", "Categoria 4")

    private lateinit var nombreView: EditText
    private lateinit var descripcionView: EditText
    private lateinit var precioView: EditText
    private lateinit var stockView: EditText
    private lateinit var categoriaView: Spinner
    private lateinit var excesoAzucaresView: CheckBox
    private lateinit var excesoSodioView: CheckBox
    private lateinit var excesoGrasasSatView: CheckBox
    private lateinit var excesiGrasasTotView: CheckBox
    private lateinit var excesoCaloriasView: CheckBox
    private lateinit var botonSacarFoto: Button
    private lateinit var botonSubirFoto: Button
    private lateinit var botonCrear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_nuevo_producto, container, false
        )

        altaProductosView = binding.root

        setViews()
        setCategorias()
        setHandlers()

        return altaProductosView
    }

    private fun setViews() {
        nombreView = altaProductosView.findViewById(R.id.nombre_producto)
        descripcionView = altaProductosView.findViewById(R.id.descripcion_producto)
        precioView = altaProductosView.findViewById(R.id.producto_precio)
        stockView = altaProductosView.findViewById(R.id.stock)
        categoriaView = altaProductosView.findViewById(R.id.categoria_list)
        excesoAzucaresView = altaProductosView.findViewById(R.id.check_box_exceso_azucares)
        excesoSodioView = altaProductosView.findViewById(R.id.check_box_exceso_sodio)
        excesoGrasasSatView = altaProductosView.findViewById(R.id.check_box_exceso_grasas_saturadas)
        excesiGrasasTotView = altaProductosView.findViewById(R.id.check_box_exceso_grasas_totales)
        excesoCaloriasView = altaProductosView.findViewById(R.id.check_box_exceso_calorias)
        botonSacarFoto = altaProductosView.findViewById(R.id.btn_sacar_foto)
        botonSubirFoto = altaProductosView.findViewById(R.id.btn_subir_foto)
        botonCrear = altaProductosView.findViewById(R.id.btn_crear)
    }

    private fun setCategorias() {
        val categoriasAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item, categorias
        )
        binding.categoriaList.adapter = categoriasAdapter
    }

    private fun setHandlers() {
        botonSacarFoto.setOnClickListener {
            Toast.makeText(context, "Sacar foto", Toast.LENGTH_SHORT).show()
        }

        botonSubirFoto.setOnClickListener {
            Toast.makeText(context, "Subir foto", Toast.LENGTH_SHORT).show()
        }

        botonCrear.setOnClickListener {
                if (formValido()) {
                    getDbReference().collection("productos")
                        .add(getProductoPayload())
                        .addOnSuccessListener {
                            Toast.makeText(context, "¡Guardado!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.nav_products)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "No se pudo guardar", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Revise los datos ingresados", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun formValido(): Boolean {
        var valido = true

        // TODO: Handle foto

        if (nombreView.text.isNullOrBlank()) {
            valido = false
        }

        if (descripcionView.text.isNullOrBlank()) {
            valido = false
        }

        if (precioView.text.isNullOrBlank()) {
            valido = false
        }

        if (stockView.text.isNullOrBlank()) {
            valido = false
        }

        if (categoriaView.selectedItem.toString().isBlank()) {
            valido = false
        }

        return valido
    }

    private fun getProductoPayload(): HashMap<String, Any> {
        return  hashMapOf(
            "nombre" to nombreView.text.toString(),
            "observaciones" to descripcionView.text.toString(),
            "precio" to precioView.text.toString().toLong(),
            "stock" to stockView.text.toString().toInt(),
            "foto" to "",
            "categoria" to categoriaView.selectedItem.toString(),
            "exceso_azucar" to excesoAzucaresView.isChecked,
            "exceso_sodio" to excesoSodioView.isChecked,
            "exceso_grasas_sat" to excesoGrasasSatView.isChecked,
            "exceso_grasas_tot" to excesiGrasasTotView.isChecked,
            "exceso_calorias" to excesoCaloriasView.isChecked,
            "tienda" to arguments?.getString("tienda").toString()
        )
    }
}