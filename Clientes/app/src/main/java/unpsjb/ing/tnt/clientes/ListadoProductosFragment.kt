package unpsjb.ing.tnt.clientes

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.clientes.adapter.ProductosAdapter
import unpsjb.ing.tnt.clientes.data.model.Carrito
import unpsjb.ing.tnt.clientes.data.model.Producto
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import unpsjb.ing.tnt.clientes.databinding.FragmentListadoProductosBinding

class ListadoProductosFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentListadoProductosBinding
    private lateinit var fragmentContext: Context
    private lateinit var listView: View
    private lateinit var userEmail: String
    private lateinit var tiendaId: String
    private lateinit var carrito: Carrito
    private lateinit var productos: List<Producto>
    private var productosSnapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listado_productos, container, false
        )
        fragmentContext = this.requireContext()
        listView = binding.root

        userEmail = arguments?.getString("email").toString()
        tiendaId = arguments?.getString("tiendaId").toString()

        if (tiendaId.isNotEmpty()) {
            crearCarrito()

            registerCarritoButton()
            registerProductosSnapshotListener(tiendaId)
            /*getDbReference().collection("productos")
                .whereEqualTo("tienda", tiendaId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.documents.isNotEmpty()) {
                        crearCarrito()

                        registerCarritoButton()
                        registerProductosSnapshotListener(documentSnapshot.documents.first().id)
                    } else {
                        // TODO: No se encontró la tienda
                    }
                }
                .addOnFailureListener {
                    // TODO: Falló al querer ir a buscar la tienda
                }*/
        } else {
            // TODO: Error, no se pasó la tienda
        }


        return listView
    }

    private fun registerCarritoButton() {
        binding.irAlCarrito.setOnClickListener {
            if (binding.irAlCarrito.isEnabled) {
                findNavController().navigate(R.id.carritoFragment, bundleOf("tienda" to tiendaId, "email" to userEmail))
            } else {
                Toast.makeText(fragmentContext, "¡Espere a que termine de actualizarse el carrito!", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun crearCarrito() {
        getDbReference().collection("carritos")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { resultCarritos ->
                    var id = "1"

                    if (resultCarritos.documents.isNotEmpty()) {
                        val lastId = (resultCarritos.documents.first().get("id") as String).toInt()
                        id = (lastId + 1).toString()
                    }

                    carrito = Carrito(
                        id,
                        userEmail,
                        arrayListOf<ProductoCarrito>(),
                        Timestamp.now(),
                        0,
                        true,
                        tiendaId
                    )
                }
                .addOnFailureListener {
                    // TODO: Mostrar un toast y redirigir al home de tiendas
                }

    }

    private fun registerProductosSnapshotListener(nombreTienda: String) {
        productosSnapshotListener?.remove()

        val selectedFilter = binding.filtroNombre.text.toString()

        productosSnapshotListener = getDbReference().collection("productos").whereEqualTo("tienda", nombreTienda)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("ListadoTiendas", e.message.toString())
                        return@addSnapshotListener
                    }

                    productos = parseProductos(snapshots, selectedFilter)
                    val adapter = ProductosAdapter(this.requireContext(),
                        productos,
                    ) { producto -> agregarAlCarrito(producto) }
                    binding.listadoProductos.adapter = adapter
                }
    }

    private fun agregarAlCarrito(producto: String) {
        if (productos.isNotEmpty()) {
            toggleButton()
            val productoObj = productos.find { it.id == producto }

            if (productoObj != null) {
                val productoCarritoList = carrito.productos.filter { it.idProducto == productoObj.id }

                if (productoCarritoList.isEmpty()) {
                    carrito.productos.add(crearProductoCarrito(productoObj))
                } else {
                    val productoCarritoObj = productoCarritoList.first()
                    productoCarritoObj.cantidad += 1
                    productoCarritoObj.precio = productoObj.precioUnitario * productoCarritoObj.cantidad
                    carrito.actualizarTotal()
                    // TODO: Actualizar leyenda del total en el botón
                }
            } else {
                // TODO: Error, el producto no se encontró
            }

            toggleButton()
        } else {
            // TODO: Error, no se habían cargado productos
        }
    }

    private fun crearProductoCarrito(producto: Producto, cantidad: Long = 1): ProductoCarrito {
        return ProductoCarrito(
            producto.id,
            cantidad,
            producto.nombre,
            producto.fotografia,
            producto.observaciones,
            producto.categoria,
            producto.precioUnitario * cantidad
        )
    }

    @SuppressLint("SetTextI18n")
    private fun toggleButton() {
        val irAlCarritoBtn = binding.irAlCarrito

        if (irAlCarritoBtn.isEnabled) {
            irAlCarritoBtn.text = GUARDANDO_CARRITO_BTN_TEXT
            irAlCarritoBtn.isEnabled = false
        } else {
            irAlCarritoBtn.text = CARRITO_DISPONIBLE_BTN_TEXT + "($" + carrito.total + ")"
            irAlCarritoBtn.isEnabled = true
        }
    }

    private fun parseProductos(snapshots: QuerySnapshot?, filtro: String): List<Producto> {
        val productos = java.util.ArrayList<Producto>()

        if (snapshots != null) {
            for (document in snapshots.documents) {
                if (!Producto.validateDocument(document)) {
                    continue
                }

                var producto: Producto? = null

                if (filtro != "") {
                    if (document.get("nombre").toString().contains(filtro, ignoreCase = true)) {
                        producto = Producto(
                                document.get("id") as String,
                                document.get("nombre") as String,
                                document.get("cantidadDisponible").toString().toLong(),
                                document.get("precioUnitario").toString().toLong(),
                                document.get("categoria") as String,
                                document.get("fotografia") as String,
                                document.get("observaciones") as String,
                                document.get("tienda") as String
                        )
                    }
                } else {
                    producto = Producto(
                            document.get("id") as String,
                            document.get("nombre") as String,
                            document.get("cantidadDisponible").toString().toLong(),
                            document.get("precioUnitario").toString().toLong(),
                            document.get("categoria") as String,
                            document.get("fotografia") as String,
                            document.get("observaciones") as String,
                            document.get("tienda") as String
                    )
                }

                if (producto != null) {
                    productos.add(producto)
                }
            }
        }

        return productos.sortedWith(compareBy { it.id })
    }

    companion object {
        const val GUARDANDO_CARRITO_BTN_TEXT = "Actualizando carrito"
        const val CARRITO_DISPONIBLE_BTN_TEXT = "Ir al Carrito"
    }
}