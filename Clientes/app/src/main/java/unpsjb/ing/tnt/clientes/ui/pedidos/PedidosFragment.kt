package unpsjb.ing.tnt.clientes.ui.pedidos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.PedidosAdapter
import unpsjb.ing.tnt.clientes.data.model.MetodoDePago
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.Producto
import unpsjb.ing.tnt.clientes.data.model.ProductoCarrito
import unpsjb.ing.tnt.clientes.databinding.FragmentPedidosBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class PedidosFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentPedidosBinding
    private lateinit var pedidosView: View
    private var pedidosSnapshotListener: ListenerRegistration? = null

    private var filtro: String = "Pendientes"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pedidos, container, false
        )

        pedidosView = binding.root

        setFiltro()
        setPedidosListener()

        return pedidosView
    }

    private fun setFiltro() {
        binding.filtro.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Pendientes", "Completados", "Cancelados")
        )

        binding.filtro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view != null) {
                    filtro = (view as TextView).text.toString()
                    setPedidosListener()
                }
            }
        }
    }

    private fun setPedidosListener() {
        pedidosSnapshotListener?.remove()

        val estado = when (filtro) {
            "Completados" -> "COMPLETADO"
            "Cancelados" -> "CANCELADO"
            else -> "PENDIENTE"
        }

        pedidosSnapshotListener = FirebaseFirestore.getInstance().collection("pedidos")
            .whereEqualTo("usuario", FirebaseAuth.getInstance().currentUser!!.uid)
            .whereEqualTo("estado", estado)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                var pedidos = listOf<Pedido>()
                if (snapshot != null && snapshot.documents.size > 0) {
                    pedidos = snapshot.documents.map { parsePedido(it) }
                }

                binding.pedidos.adapter = PedidosAdapter(
                    requireContext(),
                    pedidos
                )
            }
    }

    private fun parsePedido(document: DocumentSnapshot) = Pedido(
        document.id,
        document.get("tienda").toString(),
        document.get("estado").toString(),
        document.get("usuario").toString(),
        document.get("total").toString().toDouble(),
        document.get("envio").toString().toDouble(),
        document.get("propina").toString().toDouble(),
        document.get("comision").toString().toDouble(),
        document.get("direccion").toString(),
        MetodoDePago(
            (document.get("metodoDePago") as HashMap<String, Any>)["tipo"].toString(),
            (document.get("metodoDePago") as HashMap<String, Any>)["datos"] as HashMap<String, Any>
        ),
        document.get("estampaDeTiempo") as Timestamp,
        parseProductos(document.get("productos") as ArrayList<HashMap<String, Any>>)
    )

    private fun parseProductos(productosCarrito: java.util.ArrayList<HashMap<String, Any>>): java.util.ArrayList<ProductoCarrito> {
        val productos: ArrayList<ProductoCarrito> = arrayListOf()

        for (producto in productosCarrito) {
            productos.add(ProductoCarrito(
                Producto(
                    (producto.get("producto") as HashMap<String, Any>).get("id").toString(),
                    (producto.get("producto") as HashMap<String, Any>).get("nombre").toString(),
                    (producto.get("producto") as HashMap<String, Any>).get("observaciones").toString(),
                    (producto.get("producto") as HashMap<String, Any>).get("stock").toString().toLong(),
                    (producto.get("producto") as HashMap<String, Any>).get("precio").toString().toLong(),
                    (producto.get("producto") as HashMap<String, Any>).get("categoria").toString(),
                    (producto.get("producto") as HashMap<String, Any>).get("foto").toString(),
                    (producto.get("producto") as HashMap<String, Any>).get("tienda").toString(),
                    (producto.get("producto") as HashMap<String, Any>).get("excesoSodio").toString().toBoolean(),
                    (producto.get("producto") as HashMap<String, Any>).get("excesoGrasasTot").toString().toBoolean(),
                    (producto.get("producto") as HashMap<String, Any>).get("excesoGrasasSat").toString().toBoolean(),
                    (producto.get("producto") as HashMap<String, Any>).get("excesoCalorias").toString().toBoolean(),
                    (producto.get("producto") as HashMap<String, Any>).get("excesoAzucar").toString().toBoolean()
                ),
                producto.get("cantidad").toString().toLong()
            ))
        }

        return productos
    }
}