package unpsjb.ing.tnt.clientes.ui.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.databinding.FragmentCheckoutBinding
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.carrito
import unpsjb.ing.tnt.clientes.ClientesApplication.Companion.pedido
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.data.model.MetodoDePago
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.Tienda
import java.math.RoundingMode

class CheckoutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var checkoutView: View

    private lateinit var tienda: Tienda

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_checkout, container, false
        )

        checkoutView = binding.root

        setTienda()
        crearPedido()
        setViewData()
        setBotonCambiarDireccion()
        setBotonCambiarFormaDePago()
        setBotonHacerPedido()

        return checkoutView
    }

    private fun setTienda() {
        FirebaseFirestore.getInstance().collection("tiendas")
            .document(carrito!!.tienda)
            .get()
            .addOnSuccessListener {
                tienda = Tienda(
                    it.id,
                    it.get("nombre") as String,
                    it.get("rubro") as String,
                    it.get("calle") as String,
                    it.get("ubicacionLatLong") as HashMap<String, Double>,
                    it.get("horario_de_atencion") as HashMap<String, String>,
                    it.get("metodos_de_pago") as ArrayList<String>
                )
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ocurrió un error", Toast.LENGTH_SHORT).show()
                (activity as HomeActivity).onBackPressed()
            }
    }

    private fun crearPedido() {
        if (pedido == null) {
            pedido = Pedido.new()
        }

        pedido!!.tienda = carrito!!.tienda
        pedido!!.usuario = FirebaseAuth.getInstance().currentUser!!.uid
        pedido!!.productos = carrito!!.productos
    }

    private fun setViewData() {
        setDireccion()
        setMetodoDePago()
        setTotalProductos()
        setTotalEnvio()
        setPropina()
        setTotalComision()
        setTotalFinal()
    }

    private fun setDireccion() {
        if (pedido!!.direccion != "") {
            binding.direccion.text = pedido!!.direccion
        } else {
            binding.direccion.text = "Elija una dirección para el envío"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setMetodoDePago() {
        if (pedido!!.metodoDePago.esValido()) {
            if (pedido!!.metodoDePago.tipo == MetodoDePago.TIPO_TARJETA) {
                binding.efectivoLayout.visibility = View.GONE
                binding.tarjetaLayout.visibility = View.VISIBLE
                binding.tarjetaText.text = "Pagas con ${pedido!!.metodoDePago.obtenerTarjetaOfuscada()}"
                binding.cuotas.adapter = ArrayAdapter(this.requireContext(),
                    android.R.layout.simple_spinner_item,
                    pedido!!.metodoDePago.obtenerCuotasParseadas()
                )

                binding.cuotas.onItemSelectedListener = object : OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        pedido!!.metodoDePago.datos["cuotas"] = 0
                        Toast.makeText(requireContext(), "¡No te olvides de seleccionar una cuota!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val cuotaUno = pedido!!.metodoDePago.obtenerCuotasParseadas().find {
                            it.split(" ")[0] == "1"
                        }

                        if (view != null) {
                            val cuota = (view as TextView).text.toString()

                            if (cuota.isNotEmpty()) {
                                pedido!!.metodoDePago.datos["cuotas"] = cuota
                            } else {
                                pedido!!.metodoDePago.datos["cuotas"] = cuotaUno!!
                            }
                        } else {
                            pedido!!.metodoDePago.datos["cuotas"] = cuotaUno!!
                        }
                    }
                }
            } else {
                binding.tarjetaLayout.visibility = View.GONE
                binding.efectivoLayout.visibility = View.VISIBLE
                binding.efectivoText.text = "Pagas en efectivo con ${pedido!!.metodoDePago.obtenerPagaCon()}"
            }
        } else {
            binding.tarjetaLayout.visibility = View.GONE
            binding.efectivoText.text = "Elija un método de pago válido"
        }
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
            checkoutView.findNavController().navigate(
                R.id.nav_payment_method,
                bundleOf(
                    "aceptaDebito" to tienda.aceptaDebito(),
                    "aceptaCredito" to tienda.aceptaCredito(),
                    "aceptaEfectivo" to tienda.aceptaEfectivo()
                )
            )
        }
    }

    private fun setBotonHacerPedido() {
        binding.pagar.setOnClickListener {
            if (pedido!!.valido()) {
                pedido!!.guardar()
                    .addOnSuccessListener {
                        ClientesApplication.reiniciarPedido()
                        Toast.makeText(context, "¡Pedido realizado!", Toast.LENGTH_SHORT).show()
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