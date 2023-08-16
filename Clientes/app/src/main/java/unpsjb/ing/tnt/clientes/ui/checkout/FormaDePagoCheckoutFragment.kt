package unpsjb.ing.tnt.clientes.ui.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.MetodoDePago
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.databinding.FragmentFormaDePagoCheckoutBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment
import unpsjb.ing.tnt.clientes.ui.utils.MaskWatcher

class FormaDePagoCheckoutFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentFormaDePagoCheckoutBinding
    private lateinit var formaDePagoView: View
    private var pedido: Pedido = ClientesApplication.pedido!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_forma_de_pago_checkout, container, false
        )

        formaDePagoView = binding.root

        setViewData()
        setCheckboxes()
        setEfectivoFormListeners()
        setTarjetaFormListeners()
        setBotonAceptarListener()

        return formaDePagoView
    }

    private fun setViewData() {
        binding.totalAPagar.text = ClientesApplication.pedido!!.total.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun setCheckboxes() {
        binding.checkboxEfectivo.setOnClickListener {
            binding.checkboxTarjeta.isChecked = false
            binding.totalAPagar.text = ClientesApplication.pedido!!.total.toString()
            binding.aceptar.text = "Pagar $${ClientesApplication.pedido!!.total} en efectivo"
            binding.tarjetaLayout.visibility = View.GONE
            binding.efectivoLayout.visibility = View.VISIBLE
            limpiarTarjetaLayout()
            pedido.metodoDePago.tipo = MetodoDePago.TIPO_EFECTIVO
            pedido.metodoDePago.datos = hashMapOf()
        }

        binding.checkboxTarjeta.setOnClickListener {
            binding.checkboxEfectivo.isChecked = false
            binding.aceptar.text = "Pagar $${ClientesApplication.pedido!!.total} con tarjeta"
            binding.efectivoLayout.visibility = View.GONE
            binding.tarjetaLayout.visibility = View.VISIBLE
            limpiarEfectivoLayout()
            pedido.metodoDePago.tipo = MetodoDePago.TIPO_TARJETA
            pedido.metodoDePago.datos = hashMapOf()
        }
    }

    private fun limpiarTarjetaLayout() {
        binding.numeroTarjeta.setText("")
        binding.vencimientoTarjeta.setText("")
        binding.cvvTarjeta.setText("")
        binding.nombreTh.setText("")
        binding.dniTh.setText("")
    }

    private fun limpiarEfectivoLayout() {
        binding.montoPago.setText("")
    }

    private fun setEfectivoFormListeners() {
        binding.montoPago.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.isNotEmpty()) {
                        val pedido = ClientesApplication.pedido!!
                        binding.aceptar.text = "Pagar $${pedido.total} en efectivo con $${binding.montoPago.text.toString().toDouble()}"

                        pedido.metodoDePago.tipo = MetodoDePago.TIPO_EFECTIVO
                        pedido.metodoDePago.datos =  hashMapOf(
                            "pagaCon" to s.toString()
                        )
                    }
                }
            }
        })
    }

    private fun setTarjetaFormListeners() {
        binding.numeroTarjeta.addTextChangedListener(MaskWatcher("#### #### #### ####"))
        binding.numeroTarjeta.setOnFocusChangeListener { _, b ->
            if (!b && binding.numeroTarjeta.text.length >= 6) {
                MetodoDePago.checkTarjeta(
                    binding.numeroTarjeta.text.toString().filterNot { it.isWhitespace() },
                    callbackError = {
                        Log.d("TARJETA", "Error")
                        // TODO: Mostrar el error
                        pedido.metodoDePago.datos = hashMapOf()
                    },
                    callbackSuccess = { tipo, red ->
                        pedido.metodoDePago.datos = hashMapOf(
                            "tipo" to tipo,
                            "red" to red
                        )
                    }
                )
            }
        }
        binding.vencimientoTarjeta.addTextChangedListener(MaskWatcher("##/##"))
    }

    private fun completarFormaDePagoData() {
        val metodoDePago = ClientesApplication.pedido!!.metodoDePago

        if (binding.checkboxTarjeta.isChecked) {
            val partesTarjeta = binding.numeroTarjeta.text.toString().split(" ")

            metodoDePago.datos["tarjeta"] =
                partesTarjeta[0] + " " + partesTarjeta[1].substring(0, 2) + "XX XXXX " + partesTarjeta[3]
            metodoDePago.datos["nombre"] = binding.nombreTh.text.toString()
            metodoDePago.datos["dni"] = binding.dniTh.text.toString()
            metodoDePago.tipo = MetodoDePago.TIPO_TARJETA
        } else {
            metodoDePago.tipo = MetodoDePago.TIPO_EFECTIVO
        }
    }

    private fun setBotonAceptarListener() {
        binding.aceptar.setOnClickListener {
            completarFormaDePagoData()

            if (metodoDePagoValido()) {
                (activity as HomeActivity).onBackPressed()
            }
        }
    }

    private fun metodoDePagoValido(): Boolean {
        return if (binding.checkboxTarjeta.isChecked) {
            tarjetaValida()
        } else {
            efectivoValido()
        }
    }

    private fun tarjetaValida(): Boolean {
        val tarjeta = binding.numeroTarjeta.text.toString().filterNot { it.isWhitespace() }
        val vencimiento = binding.vencimientoTarjeta.text.toString().filterNot { it == '/' }
        val cvv = binding.cvvTarjeta.text.toString()
        val nombre = binding.nombreTh.text.toString()
        val dni = binding.dniTh.text.toString()
        val camposConError = arrayListOf<String>()

        if (tarjeta.length < 15) {
            camposConError.add("Tarjeta")
        }

        if (vencimiento.length < 4) {
            camposConError.add("Vencimiento")
        }

        if (cvv.length < 3) {
            camposConError.add("CVV")
        }

        if (nombre.isEmpty()) {
            camposConError.add("Nombre")
        }

        if (dni.isEmpty()) {
            camposConError.add("DNI")
        }

        if (camposConError.isNotEmpty()) {
            Toast.makeText(context, "Los siguientes campos no estan correctos: "  + camposConError.joinToString { it }, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun efectivoValido(): Boolean {
        if (binding.montoPago.text.toString().isEmpty()) {
            Toast.makeText(context, "Tenes que aclarar con cuanto lo pagas cuando llegue", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}