package unpsjb.ing.tnt.clientes.ui.checkout

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.databinding.FragmentCheckoutBinding
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.carrito
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.pedido
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.Producto

class CheckoutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var checkoutView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_checkout, container, false
        )

        checkoutView = binding.root

        crearPedido()
        setViewData()
        setBotonCambiarDireccion()
        setBotonCambiarFormaDePago()
        setBotonHacerPedido()

        return checkoutView
    }

    private fun crearPedido() {
        pedido = Pedido.new(
            tienda = carrito!!.tienda,
            usuario = FirebaseAuth.getInstance().currentUser!!.uid,
            productos = carrito!!.productos.map { it.producto } as ArrayList<Producto>
        )
    }

    private fun setViewData() {
        setDireccion()
        setTotalProductos()
        setTotalEnvio()
        setPropina()
        setTotalComision()
        setTotalFinal()
    }

    private fun setDireccion() {
        FirebaseFirestore.getInstance().collection("direcciones")
    }

    private fun setTotalFinal() {
        val totalProductos = if (binding.totalProductos.text.toString().isNotEmpty()) {
            binding.totalProductos.text.toString().toDouble()
        } else {
            0.0
        }

        val totalEnvio = if (binding.totalEnvio.text.toString().isNotEmpty()) {
            binding.totalEnvio.text.toString().toDouble()
        } else {
            0.0
        }
        pedido!!.envio = totalEnvio

        val totalPropina = if (binding.totalPropina.text.toString().isNotEmpty()) {
            binding.totalPropina.text.toString().toDouble()
        } else {
            0.0
        }
        pedido!!.propina = totalPropina

        val totalComision = if (binding.totalComision.text.toString().isNotEmpty()) {
            binding.totalComision.text.toString().toDouble()
        } else {
            0.0
        }
        pedido!!.comision = totalComision

        val total = totalProductos + totalEnvio + totalPropina + totalComision

        binding.totalFinal.text = total.toString()
        pedido!!.total = total
    }

    private fun setTotalComision() {
        binding.totalComision.text = "200.0"
        pedido!!.comision = 200.0
    }

    private fun setTotalEnvio() {
        binding.totalEnvio.text = "500.0"
        pedido!!.envio = 500.0
    }

    private fun setTotalProductos() {
        binding.totalProductos.text = carrito!!.productos.sumOf {
            it.producto.precio * it.cantidad
        }.toDouble().toString()
    }

    private fun setPropina() {
        binding.propinaDefault.text = carrito!!.getPropinaSugerida().toString()
        binding.propinaDefaultCheckbox.isChecked = true
        binding.totalPropina.text = carrito!!.getPropinaSugerida().toString()

        binding.propinaDefaultCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.propinaDefaultCheckbox.isChecked = isChecked

            if (isChecked) {
                binding.totalPropina.text = carrito!!.getPropinaSugerida().toString()
            } else {
                if (binding.propinaUsuario.text.isEmpty()) {
                    binding.totalPropina.text = "0.0"
                } else {
                    binding.totalPropina.text = binding.propinaUsuario.text
                }
            }

            setTotalFinal()
        }

        binding.propinaUsuario.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.propinaDefaultCheckbox.isChecked) {
                    binding.propinaDefaultCheckbox.isChecked = false
                }

                binding.totalPropina.text = s
                setTotalFinal()
            }
        })
    }

    private fun setBotonCambiarDireccion() {
        binding.elegirDireccion.setOnClickListener {
            checkoutView.findNavController().navigate(R.id.nav_addresses_checkout)
        }
    }

    private fun setBotonCambiarFormaDePago() {
        binding.elegirFormaDePago.setOnClickListener {
            checkoutView.findNavController().navigate(R.id.nav_payment_method)
        }
    }

    private fun setBotonHacerPedido() {
        binding.pagar.setOnClickListener {
            if (pedido!!.valido()) {
                pedido!!.guardar()
                    .addOnSuccessListener {
                        Toast.makeText(context, "¡Listo!", Toast.LENGTH_SHORT).show()
                        checkoutView.findNavController().navigate(R.id.nav_home)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Ocurrió un error, por favor reintente", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Asegurate de haber completado todos los datos", Toast.LENGTH_SHORT).show()
            }

        }
    }
}