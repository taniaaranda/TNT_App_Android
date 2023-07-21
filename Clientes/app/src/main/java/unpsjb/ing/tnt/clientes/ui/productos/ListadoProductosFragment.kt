package unpsjb.ing.tnt.clientes.ui.productos

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.carrito
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.ProductosAdapter
import unpsjb.ing.tnt.clientes.data.model.Carrito
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.Producto
import unpsjb.ing.tnt.clientes.databinding.FragmentListadoProductosBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class ListadoProductosFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentListadoProductosBinding
    private lateinit var fragmentContext: Context
    private lateinit var listadoProductosView: View

    private var tiendaId: String? = null
    private var filtroProducto: String? = null

    private lateinit var usuario: FirebaseUser
    private lateinit var productos: ArrayList<Producto>
    private lateinit var recyclerView: RecyclerView
    private lateinit var productosAdapter: ProductosAdapter

    private var productosSnapshotListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listado_productos, container, false
        )
        fragmentContext = this.requireContext()

        listadoProductosView = binding.root

        recyclerView = listadoProductosView.findViewById(R.id.listado_productos)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = layoutManager

        usuario = FirebaseAuth.getInstance().currentUser!!

        setUpView()

        return listadoProductosView
    }

    private fun setUpView() {
        setTienda()
        setCarrito()
        setListeners()
    }

    private fun setCarrito() {
        if (carrito == null || carrito?.tienda != tiendaId.toString()) {
            carrito = Carrito.new(usuario.uid, tiendaId.toString())
        }
    }

    private fun setListeners() {
        productos = ArrayList()
        productosAdapter = ProductosAdapter(
            requireContext(),
            productos,
            carrito!!.productos,
            callbackAgregar = { agregarAlCarrito(it) },
            callbackQuitar = {  quitarDelCarrito(it) }
        )

        recyclerView.adapter = productosAdapter

        handleCarrito()
        setFiltroProducto()
        setCarritoIconListener()
        registerProductosSnapshotListener()
    }

    private fun setTienda() {
        if (arguments != null) {
            tiendaId = requireArguments().getString("tiendaId").toString()
        }
    }

    private fun setFiltroProducto() {
        binding.filtroProducto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtroProducto = if (s == "") {
                    null
                } else {
                    s.toString()
                }

                registerProductosSnapshotListener()
            }
        })
    }

    private fun setCarritoIconListener() {
        binding.cartContainer.setOnClickListener {
            irAlCarrito()
        }

        binding.irAlCarrito.setOnClickListener {
            irAlCarrito()
        }
    }

    private fun irAlCarrito() {
        if (!carrito!!.estaGuardado()) {
            carrito!!.guardar(OnCompleteListener {
                if (it.isSuccessful) {
                    carrito!!.id = it.result.id
                    view?.findNavController()?.navigate(R.id.nav_cart)
                } else {
                    Toast.makeText(context, "No se pudo guardar el carrito", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            view?.findNavController()?.navigate(R.id.nav_cart)
        }
    }

    private fun registerProductosSnapshotListener() {
        productosSnapshotListener?.remove()

        if (tiendaId == null) {
            Toast.makeText(context, "Ocurrió un error", Toast.LENGTH_SHORT).show()
            return
        }

        productosSnapshotListener = FirebaseFirestore.getInstance()
            .collection("productos")
            .whereEqualTo("tienda", tiendaId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ListadoTiendas", e.message.toString())
                    return@addSnapshotListener
                }

                Log.d("Productos Count", snapshots?.documents?.count().toString())
                handleProductos(snapshots)
            }
    }

    private fun handleProductos(snapshots: QuerySnapshot?) {
        productos.clear()  // Esto se debería hacer mucho mejor

        if (snapshots != null) {
            for (document in snapshots.documents) {
                val producto = Producto.hidratar(document)

                if (filtroProducto != null && !document.get("nombre").toString().contains(filtroProducto.toString())) {
                    continue
                }

                if (productos.contains(producto)) {
                    continue
                }

                productos.add(producto)
            }
        }

        handleUpdateProductosDataset()
    }

    private fun handleCarrito() {
        listadoProductosView.findViewById<TextView>(R.id.cart_count).text =
            carrito!!.getCantidadTotal().toString()
        handleUpdateCarritoDataset()
    }

    private fun agregarAlCarrito(producto: Producto) {
        carrito!!.agregarAlCarrito(producto)
        guardarCarrito()
    }

    private fun quitarDelCarrito(producto: Producto) {
        carrito!!.quitarDelCarrito(producto)
        guardarCarrito()
    }

    private fun guardarCarrito() {
        if (carrito!!.estaGuardado()) {
            carrito!!.actualizar(actualizadoListener())
        } else {
            handleCarrito()
        }
    }

    private fun actualizadoListener(): OnCompleteListener<Void> {
        return OnCompleteListener {
            if (it.isSuccessful) {
                handleCarrito()
            } else {
                Toast.makeText(context, "No se pudo actualizar el carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleUpdateProductosDataset() {
        productosAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleUpdateCarritoDataset() {
        productosAdapter.notifyDataSetChanged()
    }
}