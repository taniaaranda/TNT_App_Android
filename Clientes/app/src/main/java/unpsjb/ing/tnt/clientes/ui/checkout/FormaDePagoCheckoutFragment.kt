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
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.MetodoDePago
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.databinding.FragmentFormaDePagoCheckoutBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment
import unpsjb.ing.tnt.clientes.ui.utils.MaskWatcher
import java.io.IOException
import kotlin.math.min

class FormaDePagoCheckoutFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentFormaDePagoCheckoutBinding
    private lateinit var formaDePagoView: View
    private var pedido: Pedido = ClientesApplication.pedido!!

    private var aceptaEfectivo: Boolean = false
    private var aceptaDebito: Boolean = false
    private var aceptaCredito: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
        aceptaDebito = requireArguments().getBoolean("aceptaDebito")
        aceptaCredito = requireArguments().getBoolean("aceptaCredito")
        aceptaEfectivo = requireArguments().getBoolean("aceptaEfectivo")
    }

    @SuppressLint("SetTextI18n")
    private fun setCheckboxes() {
        if ((aceptaCredito || aceptaDebito) && !aceptaEfectivo) {
            binding.checkboxEfectivo.visibility = View.GONE
            binding.checkboxTarjeta.visibility = View.GONE

            binding.efectivoLayout.visibility = View.GONE
            binding.tarjetaLayout.visibility = View.VISIBLE

            binding.checkboxEfectivo.isChecked = false
            binding.checkboxTarjeta.isChecked = true
            limpiarEfectivoLayout()
            pedido.metodoDePago.tipo = MetodoDePago.TIPO_TARJETA
            pedido.metodoDePago.datos = hashMapOf()

            binding.formaDePagoTitulo.text = "Pago con tarjeta"
            binding.aceptar.text = "Pagar $${ClientesApplication.pedido!!.total} con tarjeta"
        } else if (aceptaEfectivo && !aceptaCredito && !aceptaDebito) {
            binding.checkboxEfectivo.visibility = View.GONE
            binding.checkboxTarjeta.visibility = View.GONE

            binding.efectivoLayout.visibility = View.VISIBLE
            binding.tarjetaLayout.visibility = View.GONE

            binding.checkboxTarjeta.isChecked = false
            binding.checkboxEfectivo.isChecked = true
            limpiarTarjetaLayout()
            pedido.metodoDePago.tipo = MetodoDePago.TIPO_EFECTIVO
            pedido.metodoDePago.datos = hashMapOf()

            binding.formaDePagoTitulo.text = "Pago en efectivo"
            binding.aceptar.text = "Pagar $${ClientesApplication.pedido!!.total} en efectivo"
        } else {
            setEfectivoCheckbox()
            setTarjetaCheckbox()
        }
    }

    private fun setEfectivoCheckbox() {
        binding.checkboxEfectivo.setOnClickListener {
            binding.error.text = ""
            binding.checkboxTarjeta.isChecked = false
            binding.totalAPagar.text = ClientesApplication.pedido!!.total.toString()
            binding.aceptar.text = "Pagar $${ClientesApplication.pedido!!.total} en efectivo"
            binding.tarjetaLayout.visibility = View.GONE
            binding.efectivoLayout.visibility = View.VISIBLE
            limpiarTarjetaLayout()
            pedido.metodoDePago.tipo = MetodoDePago.TIPO_EFECTIVO
            pedido.metodoDePago.datos = hashMapOf()
        }
    }

    private fun setTarjetaCheckbox() {
        binding.checkboxTarjeta.setOnClickListener {
            binding.error.text = ""
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
            pedido.metodoDePago.datos["tarjeta"] = binding.numeroTarjeta.text.filterNot { it.isWhitespace() }
            pedido.metodoDePago.chequearBin()
        }
        binding.vencimientoTarjeta.addTextChangedListener(MaskWatcher("##/##"))
    }

    private fun setBotonAceptarListener() {
        binding.aceptar.setOnClickListener {
            if (binding.checkboxEfectivo.isChecked) {
                val pagaCon = binding.montoPago.text

                if (pagaCon.isNotEmpty()) {
                    if (pagaCon.toString().toDouble() < pedido.total) {
                        binding.error.text = "El monto del pago debe ser igual o superior al total"
                    } else {
                        binding.error.text = ""
                        pedido.metodoDePago.datos["pagaCon"] = pagaCon.toString()
                        pedido.metodoDePago.tipo = MetodoDePago.TIPO_EFECTIVO
                        (activity as HomeActivity).onBackPressed()
                    }
                }
            } else {
                binding.numeroTarjeta.clearFocus()

                if (formTarjetaEsValido()) {
                    val partesTarjeta = binding.numeroTarjeta.text.toString().split(" ")

                    pedido.metodoDePago.datos["tarjeta"] =
                        partesTarjeta[0] + " " + partesTarjeta[1].substring(0, 2) + "XX XXXX " + partesTarjeta[3]
                    pedido.metodoDePago.datos["nombre"] = binding.nombreTh.text.toString()
                    pedido.metodoDePago.datos["dni"] = binding.dniTh.text.toString()
                    pedido.metodoDePago.tipo = MetodoDePago.TIPO_TARJETA

                    if (!pedido.metodoDePago.datos.containsKey("tipo") || !pedido.metodoDePago.datos.containsKey("red")) {
                        binding.error.text = "La tarjeta ingresada no es valida"
                    } else if (pedido.metodoDePago.datos["tipo"] == "debit" && !aceptaDebito) {
                        binding.error.text = "El local no acepta debito"
                    } else if (pedido.metodoDePago.datos["tipo"] == "credit" && !aceptaCredito) {
                        binding.error.text = "El local no acepta credito"
                    } else {
                        binding.error.text = ""
                        (activity as HomeActivity).onBackPressed()
                    }
                }
            }
        }
    }

    fun formTarjetaEsValido(): Boolean {
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
}