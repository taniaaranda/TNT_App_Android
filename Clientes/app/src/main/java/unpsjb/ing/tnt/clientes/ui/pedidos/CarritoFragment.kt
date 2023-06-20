package unpsjb.ing.tnt.clientes.ui.pedidos

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.carrito
import unpsjb.ing.tnt.clientes.ui.utils.FirebaseConnectedFragment
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.ProductosCarritoAdapter
import unpsjb.ing.tnt.clientes.data.model.Carrito
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import unpsjb.ing.tnt.clientes.databinding.FragmentCarritoBinding

class CarritoFragment : FirebaseConnectedFragment() {
    private lateinit var binding: FragmentCarritoBinding
    private lateinit var carritoView: View
    private lateinit var carritoId: String

    private lateinit var productos: ArrayList<ProductoCarrito>
    private lateinit var recyclerView: RecyclerView
    private lateinit var productosAdapter: ProductosCarritoAdapter

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_carrito, container, false
        )

        carritoView = binding.root
        updateBotonPedido()
        setPedidoButton()

        setRecyclerView()
        setPedidoButtonHandler()

        return carritoView
    }

    private fun setRecyclerView() {
        recyclerView = carritoView.findViewById(R.id.listado_productos_carrito)
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext()
        )

        productosAdapter = ProductosCarritoAdapter(
            requireContext(),
            carrito!!.productos,
            callbackAgregar = { agregarCantidadAlProducto(it) },
            callbackQuitar = { quitarCantidadAlProducto(it) }
        )
        recyclerView.adapter = productosAdapter
        setCarrito()
    }

    private fun agregarCantidadAlProducto(productoCarrito: ProductoCarrito) {
        carrito!!.agregarAlCarrito(productoCarrito.producto)
        updateBotonPedido()
        refreshProductosAdapter()
    }

    private fun quitarCantidadAlProducto(productoCarrito: ProductoCarrito) {
        carrito!!.quitarDelCarrito(productoCarrito.producto)
        updateBotonPedido()
        refreshProductosAdapter()
    }

    private fun setCarrito() {
        if (arguments != null) {
            carritoId = requireArguments().getString("carritoId").toString()

            if (carritoId == "") {
                return
            }

            productos.clear()
            getDbReference().collection("carritos")
                .document(carritoId)
                .get()
                .addOnSuccessListener { result ->
                    carrito = Carrito.hidratar(result)
                    refreshProductosAdapter()
                }
                .addOnFailureListener { exception ->
                    Log.d("CarritoFragment", exception.message.toString())
                    Toast.makeText(requireContext(), "¡Ups! Ocurrió un error recuperando el carrito, volvé a intentar", Toast.LENGTH_SHORT).show()
                }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshProductosAdapter() {
        setPedidoButton()
        productosAdapter.notifyDataSetChanged()
    }

    private fun setPedidoButton() {
        carritoView.findViewById<Button>(R.id.hacer_pedido).isEnabled =
            carrito!!.productos.isNotEmpty()
    }

    private fun updateBotonPedido() {
        carritoView.findViewById<Button>(R.id.hacer_pedido).text = "Pagar ${carrito!!.getTotal()}"
    }

    private fun setPedidoButtonHandler() {
        binding.hacerPedido.setOnClickListener {
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
                        Toast.makeText(
                            requireContext(),
                            "No se pudo crear el pedido, por favor reintente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "No se pudo crear el pedido, por favor reintente",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun crearObjetoPedido(id: String = "1"): Pedido? {
        val productos = binding.productos as ArrayList<ProductoCarrito>

        if (productos.isNotEmpty()) {
            return Pedido(
                id,
                "creado",
                Timestamp.now(),
                productos.map { it.producto.id } as ArrayList<String>
            )
        }

        return null
    }

    private fun guardarPedido(pedido: Pedido) {
        getDbReference().collection("pedidos")
            .add(pedido)
            .addOnSuccessListener { document ->
                Toast.makeText(requireParentFragment().requireContext(), "¡Pedido Realizado, muchas gracias!", Toast.LENGTH_SHORT).show()
                val email = arguments?.getString("email")
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "No se pudo crear el pedido, por favor reintente", Toast.LENGTH_LONG).show()
            }
    }
}