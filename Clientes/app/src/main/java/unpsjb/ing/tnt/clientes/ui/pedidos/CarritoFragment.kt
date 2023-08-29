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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.carrito
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.ProductosCarritoAdapter
import unpsjb.ing.tnt.clientes.data.model.Carrito
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import unpsjb.ing.tnt.clientes.databinding.FragmentCarritoBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class CarritoFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentCarritoBinding
    private lateinit var carritoView: View
    private lateinit var carritoId: String

    private lateinit var productos: ArrayList<ProductoCarrito>
    private lateinit var recyclerView: RecyclerView
    private lateinit var productosAdapter: ProductosCarritoAdapter

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
            callbackAgregar = { agregarCantidadAlProducto() },
            callbackQuitar = { quitarCantidadAlProducto() }
        )
        recyclerView.adapter = productosAdapter
        setCarrito()
    }

    private fun agregarCantidadAlProducto() {
        updateBotonPedido()
        refreshProductosAdapter()
    }

    private fun quitarCantidadAlProducto() {
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
            FirebaseFirestore.getInstance().collection("carritos")
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
            carritoView.findNavController().navigate(R.id.nav_checkout)
        }
    }
}