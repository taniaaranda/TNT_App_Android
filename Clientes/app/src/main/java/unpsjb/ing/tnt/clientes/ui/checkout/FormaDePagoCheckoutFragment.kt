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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Api.Client
import kotlinx.coroutines.Runnable
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.CuotasAdapter
import unpsjb.ing.tnt.clientes.data.model.MetodoDePago
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.databinding.FragmentFormaDePagoCheckoutBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment
import unpsjb.ing.tnt.clientes.ui.utils.MaskWatcher

class FormaDePagoCheckoutFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentFormaDePagoCheckoutBinding
    private lateinit var formaDePagoView: View
    private var pedido: Pedido = ClientesApplication.pedido!!
    private var formaDePagoData: HashMap<String, Any> = hashMapOf()

    private lateinit var cuotasList: List<HashMap<String, Double>>
    private lateinit var cuotasAdapter: CuotasAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_forma_de_pago_checkout, container, false
        )

        formaDePagoView = binding.root

        setCuotasAdapter()
        setCheckboxes()
        setEfectivoFormListeners()
        setTarjetaFormListeners()
        setBotonAceptarListener()

        return formaDePagoView
    }

    private fun setCuotasAdapter() {
        recyclerView = formaDePagoView.findViewById(R.id.cuotas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        cuotasAdapter = CuotasAdapter(
            requireContext(),
            ClientesApplication.pedido!!.total,
            listOf(),
        )

        recyclerView.adapter = cuotasAdapter
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
            if (!b) {
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
            metodoDePago.datos["tarjeta"] = binding.numeroTarjeta.text.toString()
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

            if (ClientesApplication.pedido!!.metodoDePago.esValido()) {
                (activity as HomeActivity).onBackPressed()
            } else {
                Toast.makeText(context, "Por favor revise los datos ingresados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}