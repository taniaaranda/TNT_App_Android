package unpsjb.ing.tnt.clientes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.clientes.adapter.ProductosCarritoAdapter
import unpsjb.ing.tnt.clientes.data.model.Carrito
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import unpsjb.ing.tnt.clientes.databinding.FragmentCarritoBinding

class CarritoFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentCarritoBinding
    private lateinit var carritoView: View
    private lateinit var fragmentContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_carrito, container, false
        )

        carritoView = binding.root
        toggleButton()
        fragmentContext = this.requireContext()

        binding.realizarPedido.setOnClickListener {
            getDbReference().collection("pedidos")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { resultPedidos ->
                    var id = "1"

                    if (resultPedidos.documents.isNotEmpty()) {
                        val lastId = (resultPedidos.documents.first().get("id") as String).toInt()
                        id = (lastId + 1).toString()
                    }

                    val pedido: Pedido? = crearObjetoPedido(id)

                    if (pedido != null) {
                        guardarPedido(pedido)
                    } else {
                        Toast.makeText(fragmentContext, "No se pudo crear el pedido, por favor reintente", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(fragmentContext, "No se pudo crear el pedido, por favor reintente", Toast.LENGTH_LONG).show()
                }

        }

        getDbReference().collection("carritos")
                .whereEqualTo("id", "1")
                .whereEqualTo("activo", true)
                .whereEqualTo("usuario", "1")  // TODO: Sacar el id del usuario de la sesion
                .get()
                .addOnSuccessListener { result ->
                    getDataForView(result)
                }
                .addOnFailureListener { exception ->
                    Log.d("CarritoFragment", exception.message.toString())
                    Toast.makeText(fragmentContext, "¡Ups! Ocurrió un error recuperando el carrito, volvé a intentar", Toast.LENGTH_SHORT).show()
                }

        return carritoView
    }

    private fun toggleButton() {
        binding.realizarPedido.isEnabled = !binding.realizarPedido.isEnabled
    }

    private fun crearObjetoPedido(id: String = "1"): Pedido? {
        val productos = binding.productos as ArrayList<ProductoCarrito>

        if (productos.isNotEmpty()) {
            return Pedido(
                id,
                "creado",
                Timestamp.now(),
                productos.map { it.idProducto } as ArrayList<String>
            )
        }

        return null
    }

    private fun guardarPedido(pedido: Pedido) {
        getDbReference().collection("pedidos")
            .add(pedido)
            .addOnSuccessListener { document ->
                Toast.makeText(requireParentFragment().requireContext(), "¡Pedido Realizado, muchas gracias!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_carritoFragment_to_homeFragment)
            }
            .addOnFailureListener {
                Toast.makeText(fragmentContext, "No se pudo crear el pedido, por favor reintente", Toast.LENGTH_LONG).show()
            }
    }

    private fun getDataForView(result: QuerySnapshot) {
        if (result.isEmpty) {
            Toast.makeText(fragmentContext, "¡Ups! No encontramos el carrito que buscabas", Toast.LENGTH_SHORT).show()
        } else {
            val carrito = result.documents.first()
            val productosCarrito = carrito.get("productos") as ArrayList<HashMap<String, *>>

            if (productosCarrito.isEmpty()) {
                Toast.makeText(fragmentContext, "¡El carrito no tiene productos!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(fragmentContext, "¡Carrito recuperado!", Toast.LENGTH_SHORT).show()

                getDbReference().collection("productos")
                    .whereIn("id", productosCarrito.map { it.get("id") })
                    .get()
                    .addOnSuccessListener { resultProductos ->
                        val productos = resultProductos.documents

                        if (productos.isEmpty()) {
                            Toast.makeText(fragmentContext, "Alguno de los productos del carrito no fue encontrado, por favor contactese con un administrador", Toast.LENGTH_SHORT).show()
                        } else {
                            val productosForView = productosDocumentsToProductoCarritoList(productos, productosCarrito)

                            binding.carrito = carritoDocumentToObject(carrito, productos, productosCarrito)
                            binding.productos = productosForView

                            val adapter = ProductosCarritoAdapter(this.requireContext(), productosForView)
                            binding.productosList.adapter = adapter

                            toggleButton()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("CarritoFragment", exception.message.toString())
                    }
            }
        }
    }

    private fun carritoDocumentToObject(
        carrito: DocumentSnapshot,
        productos: List<DocumentSnapshot>,
        productosCarrito: ArrayList<HashMap<String, *>>
    ): Carrito {
        val productosCarritoList = productosDocumentsToProductoCarritoList(productos, productosCarrito)

        return Carrito(
            carrito.get("id") as String,
            carrito.get("usuario") as String,
            productosCarritoList,
            carrito.get("fechaCreacion") as Timestamp,
            getTotalCarrito(productosCarritoList),
            carrito.get("activo") as Boolean,
        )
    }

    private fun getTotalCarrito(productosCarritoList: java.util.ArrayList<ProductoCarrito>): Long {
        var total: Long = 0

        for (productoCarrito in productosCarritoList) {
            total += productoCarrito.precio * productoCarrito.cantidad
        }

        return total
    }

    private fun productosDocumentsToProductoCarritoList(
        productosSnapshot: List<DocumentSnapshot>,
        productosCarrito: ArrayList<HashMap<String, *>>
    ): ArrayList<ProductoCarrito> {
        val productos = ArrayList<ProductoCarrito>()

        for (productoSnapshot in productosSnapshot) {
            val productoCarrito = productosCarrito.firstOrNull { it.get("id") == productoSnapshot.get("id") }

            if (productoCarrito != null && productoCarrito.containsKey("cantidad")) {
                productos.add(ProductoCarrito(
                    productoSnapshot.get("id") as String,
                    productoCarrito["cantidad"]!! as Long,
                    productoSnapshot.get("nombre") as String,
                    productoSnapshot.get("fotografia") as String,
                    productoSnapshot.get("observaciones") as String,
                    productoSnapshot.get("categoria") as String,
                    productoSnapshot.get("precioUnitario") as Long
                ))
            } else {
                // TODO: Handle
            }
        }

        return productos
    }
}