package unpsjb.ing.tnt.vendedores.ui.productos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import unpsjb.ing.tnt.vendedores.ui.utils.FirebaseConnectedFragment
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.VendedoresApplication
import unpsjb.ing.tnt.vendedores.databinding.FragmentNuevoProductoBinding
private var selectedImageUri: Uri? = null
private val file = 1
private var URLimage: String? = null

@Suppress("DEPRECATION")
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == file && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                selectedImageUri = imageUri
            } else {
                Toast.makeText(context, "Error al obtener la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setViews() {
        nombreView = altaProductosView.findViewById(R.id.nombre_nuevo_producto)
        descripcionView = altaProductosView.findViewById(R.id.descripcion_nuevo_producto)
        precioView = altaProductosView.findViewById(R.id.precio_nuevo_producto)
        stockView = altaProductosView.findViewById(R.id.stock_nuevo_producto)
        categoriaView = altaProductosView.findViewById(R.id.categorias_list)
        excesoAzucaresView = altaProductosView.findViewById(R.id.check_box_exceso_azucares)
        excesoSodioView = altaProductosView.findViewById(R.id.check_box_exceso_sodio)
        excesoGrasasSatView = altaProductosView.findViewById(R.id.check_box_exceso_grasas_saturadas)
        excesiGrasasTotView = altaProductosView.findViewById(R.id.check_box_exceso_grasas_totales)
        excesoCaloriasView = altaProductosView.findViewById(R.id.check_box_exceso_calorias)
        botonSubirFoto = altaProductosView.findViewById(R.id.btn_subir_foto)
        botonCrear = altaProductosView.findViewById(R.id.btn_crear)
    }

    private fun setCategorias() {
        val categoriasAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item, categorias
        )
        binding.categoriasList.adapter = categoriasAdapter
    }

    private fun setHandlers() {
        botonSubirFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            //intent.type = "image/*" // Filtrar solo imágenes
            startActivityForResult(intent, file) // Llamada a startActivityForResult
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
        URLimage = uploadImageAndSaveToFirestore()
        return  hashMapOf(
            "nombre" to nombreView.text.toString(),
            "observaciones" to descripcionView.text.toString(),
            "precio" to precioView.text.toString().toLong(),
            "stock" to stockView.text.toString().toInt(),
            "foto" to URLimage.toString(),
            "categoria" to categoriaView.selectedItem.toString(),
            "exceso_azucar" to excesoAzucaresView.isChecked,
            "exceso_sodio" to excesoSodioView.isChecked,
            "exceso_grasas_sat" to excesoGrasasSatView.isChecked,
            "exceso_grasas_tot" to excesiGrasasTotView.isChecked,
            "exceso_calorias" to excesoCaloriasView.isChecked,
            "tienda" to VendedoresApplication.tienda!!.id
        )
    }

    private fun uploadImageAndSaveToFirestore(): String {
        var URLimage = ""
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            //val imageRef = storageRef.child("images/${selectedImageUri?.lastPathSegment}")
            //val imageRef = storageRef.child("${selectedImageUri?.lastPathSegment}")
            //val imageName = "img.jpg" // Cambia el nombre y la extensión según tus necesidades
            val imageName = "img${currentNumber()}.jpg"
            val imageRef = storageRef.child(imageName)
            val uploadTask = imageRef.putFile(selectedImageUri!!)
            if (imageRef.toString() != "") {
                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageURL = uri.toString()
                        URLimage = imageURL
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Error al obtener la URL de la imagen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
            }
            URLimage = imageRef.toString()
            return URLimage
        }
        return URLimage
    }

    private fun currentNumber(): Int? {
        val sharedPreferences = context?.getSharedPreferences("image_prefs", Context.MODE_PRIVATE)
        val currentNumber = sharedPreferences?.getInt("image_number", 1)
        // Incrementar el número y guardar en SharedPreferences
        val newNumber = currentNumber?.plus(1)
        if (newNumber != null) {
            sharedPreferences?.edit()?.putInt("image_number", newNumber)?.apply()
        }

        return currentNumber
    }

}