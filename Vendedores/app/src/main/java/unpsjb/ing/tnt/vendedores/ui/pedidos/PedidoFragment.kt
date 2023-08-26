package unpsjb.ing.tnt.vendedores.ui.pedidos

import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.vendedores.HomeActivity
import unpsjb.ing.tnt.vendedores.R
import unpsjb.ing.tnt.vendedores.data.model.MetodoDePago
import unpsjb.ing.tnt.vendedores.data.model.Pedido
import unpsjb.ing.tnt.vendedores.databinding.FragmentPedidoBinding
import unpsjb.ing.tnt.vendedores.excepciones.TransicionPedidoInvalidException

class PedidoFragment : Fragment() {
    private lateinit var binding: FragmentPedidoBinding
    private lateinit var pedidoView: View

    private lateinit var pedido: Pedido

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pedido, container, false
        )

        pedidoView = binding.root

        getPedidoAndSetData()

        return pedidoView
    }

    private fun setColapsos() {
        binding.colapsarProductos.setOnClickListener {
            if (binding.productos.visibility == View.GONE) {
                binding.productos.visibility = View.VISIBLE
                binding.colapsarProductos.background =
                    requireContext().getDrawable(R.drawable.arrow_up_filled)
            } else {
                binding.productos.visibility = View.GONE
                binding.colapsarProductos.background =
                    requireContext().getDrawable(R.drawable.arrow_down_filled)
            }
        }

        binding.colapsarMetodoDePago.setOnClickListener {
            val layout = if (pedido.metodoDePago.tipo == MetodoDePago.TIPO_EFECTIVO) {
                binding.efectivoLayout
            } else {
                binding.tarjetaLayout
            }

            if (layout.visibility == View.GONE) {
                layout.visibility = View.VISIBLE
                binding.colapsarMetodoDePago.background =
                    requireContext().getDrawable(R.drawable.arrow_up_filled)
            } else {
                layout.visibility = View.GONE
                binding.colapsarMetodoDePago.background =
                    requireContext().getDrawable(R.drawable.arrow_down_filled)
            }
        }

        binding.colapsarDetalles.setOnClickListener {
            if (binding.detalles.visibility == View.GONE) {
                binding.detalles.visibility = View.VISIBLE
                binding.colapsarDetalles.background =
                    requireContext().getDrawable(R.drawable.arrow_up_filled)
            } else {
                binding.detalles.visibility = View.GONE
                binding.colapsarDetalles.background =
                    requireContext().getDrawable(R.drawable.arrow_down_filled)
            }
        }
    }

    private fun getPedidoAndSetData() {
        if (arguments == null) {
            Toast.makeText(context, "Ocurrió un error", Toast.LENGTH_SHORT).show()
            (activity as HomeActivity).onBackPressed()
        }

        val pedidoId = requireArguments().getString("pedido").toString()

        if (pedidoId == "") {
            Toast.makeText(context, "Ocurrió un error interno", Toast.LENGTH_SHORT).show()
            (activity as HomeActivity).onBackPressed()
        }

        FirebaseFirestore.getInstance().collection("pedidos")
            .document(pedidoId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(context, "Ocurrió un error obteniendo los datos del pedido", Toast.LENGTH_SHORT).show()
                    (activity as HomeActivity).onBackPressed()
                }

                if (value != null) {
                    pedido = Pedido.hidratar(value)
                    setColapsos()
                    setBotones()
                    setViewData(value)
                    setBotonesListeners()
                } else {
                    Toast.makeText(context, "Ocurrió un error obteniendo el pedido", Toast.LENGTH_SHORT).show()
                    (activity as HomeActivity).onBackPressed()
                }
            }
    }

    private fun setViewData(pedidoDocument: DocumentSnapshot) {
        FirebaseFirestore.getInstance().collection("tiendas")
            .document(pedidoDocument.get("tienda").toString())
            .get()
            .addOnSuccessListener { tiendaDocument ->
                binding.nombreTienda.text = tiendaDocument.get("nombre").toString()
                binding.estado.text = pedidoDocument.get("estado").toString()
                binding.fechaPedido.text = DateFormat.format("dd/MM/yyyy hh:mm:ss", (pedidoDocument.get("estampaDeTiempo") as Timestamp).toDate())
                // TODO: Set de productos en el list con su adapter y blablabla

                val metodoDePago = pedidoDocument.get("metodoDePago") as HashMap<String, Any>
                if (metodoDePago["tipo"] == MetodoDePago.TIPO_TARJETA) {
                    val metodoDePagoData = metodoDePago["datos"] as HashMap<String, String>

                    binding.tarjeta.text = metodoDePagoData["tarjeta"]
                    binding.tipoPagoTarjeta.text =
                        if (metodoDePagoData["tipo"] == "debit") {
                            "Debito"
                        } else {
                            "Credito"
                        }
                    binding.redTarjeta.text = metodoDePagoData["red"]
                    binding.cuotasTarjeta.text = metodoDePagoData["cuotas"]
                    binding.thNombre.text = metodoDePagoData["nombre"]
                    binding.thDni.text = metodoDePagoData["dni"]
                } else {
                    binding.efectivoPagaCon.text = (metodoDePago["datos"] as HashMap<String, String>)["pagaCon"]
                }

                binding.resumenPedidoTotalProductos.text = ""  // TODO: Sacar del listado de productos
                binding.resumenPedidoTotalEnvio.text = pedidoDocument.get("envio").toString()
                binding.resumenPedidoTotalPropina.text = pedidoDocument.get("propina").toString()
                binding.resumenPedidoTotalComision.text = pedidoDocument.get("comision").toString()
                binding.resumenPedidoTotalFinal.text = pedidoDocument.get("total").toString()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ocurrió un error con la tienda", Toast.LENGTH_SHORT).show()
                (activity as HomeActivity).onBackPressed()
            }
    }

    private fun setBotones() {
        if (pedido.estaCancelado() || pedido.estaCompletado()) {
            binding.botoneraEstados.visibility = View.GONE
        } else {
            val estadoSiguiente = pedido.estadoSiguiente()
            val estadoAnterior = pedido.estadoAnterior()

            if (estadoSiguiente != null) {
                binding.avanzarEstado.text = Pedido.getStateByKey(estadoSiguiente)
                binding.avanzarEstado.isClickable = true
            } else {
                binding.avanzarEstado.isClickable = false
            }

            if (estadoAnterior != null) {
                binding.retrocederEstado.text = Pedido.getStateByKey(estadoAnterior)
                binding.retrocederEstado.setBackgroundColor(requireContext().getColor(R.color.quantum_googred600))
                binding.retrocederEstado.isClickable = true
            } else {
                binding.retrocederEstado.text = Pedido.getStateByKey(Pedido.ESTADO_PENDIENTE)
                binding.retrocederEstado.setBackgroundColor(requireContext().getColor(R.color.gray))
                binding.retrocederEstado.isClickable = false
            }
        }
    }

    private fun setBotonesListeners() {
        binding.avanzarEstado.setOnClickListener {
            try {
                if (pedido.avanzarEstado()) {
                    guardarPedido()
                } else {
                    Toast.makeText(requireContext(), "Ya esta en el estado final", Toast.LENGTH_SHORT).show()
                }
            } catch (e: TransicionPedidoInvalidException) {
                Toast.makeText(requireContext(), "Transición de estado invalida", Toast.LENGTH_SHORT).show()
            }
        }

        binding.retrocederEstado.setOnClickListener {
            try {
                if (pedido.retrocederEstado()) {
                    guardarPedido()
                } else {
                    Toast.makeText(requireContext(), "Ya esta en el estado inicial", Toast.LENGTH_SHORT).show()
                }
            } catch (e: TransicionPedidoInvalidException) {
                Toast.makeText(requireContext(), "Transición de estado invalida", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelar.setOnClickListener {
            try {
                pedido.cambiarDeEstadoA(Pedido.ESTADO_CANCELADO)
                guardarPedido()
            } catch (e: TransicionPedidoInvalidException) {
                Toast.makeText(requireContext(), "Transición de estado invalida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarPedido() {
        FirebaseFirestore.getInstance().collection("pedidos")
            .document(pedido.id)
            .update("estado", pedido.estado)
            .addOnSuccessListener {
                pedido.notificarEstado(requireContext())
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "No se pudo actualizar el pedido", Toast.LENGTH_SHORT).show()
            }
    }
}