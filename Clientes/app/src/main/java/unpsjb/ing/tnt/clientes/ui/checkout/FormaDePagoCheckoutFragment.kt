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
import unpsjb.ing.tnt.clientes.ClientesApplication
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.CuotasAdapter
import unpsjb.ing.tnt.clientes.data.model.MetodoDePago
import unpsjb.ing.tnt.clientes.databinding.FragmentFormaDePagoCheckoutBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment
import unpsjb.ing.tnt.clientes.ui.utils.MaskWatcher

class FormaDePagoCheckoutFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentFormaDePagoCheckoutBinding
    private lateinit var formaDePagoView: View
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
            formaDePagoData = hashMapOf()
        }

        binding.checkboxTarjeta.setOnClickListener {
            binding.checkboxEfectivo.isChecked = false
            binding.aceptar.text = "Pagar $${ClientesApplication.pedido!!.total} con tarjeta"
            binding.efectivoLayout.visibility = View.GONE
            binding.tarjetaLayout.visibility = View.VISIBLE
            limpiarEfectivoLayout()
            formaDePagoData = hashMapOf()
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
                        binding.aceptar.text = "Pagar $${ClientesApplication.pedido!!.total} en efectivo con $${binding.montoPago.text.toString().toDouble()}"
                        formaDePagoData["pagaCon"] = s.toString()
                    }
                }
            }
        })
    }

    private fun setTarjetaFormListeners() {
        binding.numeroTarjeta.addTextChangedListener(MaskWatcher("#### #### #### ####"))
        binding.numeroTarjeta.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length >= 6) {
                        MetodoDePago.checkTarjeta(
                            s.toString().filterNot { it.isWhitespace() },
                            callbackError = {
                                Log.d("TARJETA", "Error")
                            },
                            callbackSuccess = { tipo, red ->
                                formaDePagoData["tipo"] = tipo
                                formaDePagoData["red"] = red

//                                if (tipo == "credit") {
//                                    agregarCuotas()
//                                } else {
//                                    quitarCuotas()
//                                }
                            })
                    }
                }
            }
        })

        binding.vencimientoTarjeta.addTextChangedListener(MaskWatcher("##/##"))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun agregarCuotas() {
        cuotasList = MetodoDePago.getCuotas()
        cuotasAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun quitarCuotas() {
        cuotasList = listOf()
        cuotasAdapter.notifyDataSetChanged()
    }

    private fun setFormaDePagoData() {
        if (binding.checkboxTarjeta.isChecked) {
            formaDePagoData["tarjeta"] = binding.numeroTarjeta.text.toString()
            formaDePagoData["nombre"] = binding.nombreTh.text.toString()
            formaDePagoData["dni"] = binding.dniTh.text.toString()
            ClientesApplication.pedido!!.metodoDePago.tipo = MetodoDePago.TIPO_TARJETA
        } else {
            ClientesApplication.pedido!!.metodoDePago.tipo = MetodoDePago.TIPO_EFECTIVO
        }

        ClientesApplication.pedido!!.metodoDePago.datos = formaDePagoData
    }

    private fun setBotonAceptarListener() {
        binding.aceptar.setOnClickListener {
            setFormaDePagoData()

            if (ClientesApplication.pedido!!.metodoDePago.esValido()) {
                Log.d("FormaDePago", "Todo ok!")
                (activity as HomeActivity).onBackPressed()
            } else {
                Toast.makeText(context, "Por favor revise los datos ingresados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}