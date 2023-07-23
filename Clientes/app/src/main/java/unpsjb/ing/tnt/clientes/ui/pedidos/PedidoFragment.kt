package unpsjb.ing.tnt.clientes.ui.pedidos

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import unpsjb.ing.tnt.clientes.HomeActivity
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.MetodoDePago
import unpsjb.ing.tnt.clientes.databinding.FragmentPedidoBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class PedidoFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentPedidoBinding
    private lateinit var pedidoView: View

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
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    setViewData(it)
                } else {
                    Toast.makeText(context, "Ocurrió un error obteniendo el pedido", Toast.LENGTH_SHORT).show()
                    (activity as HomeActivity).onBackPressed()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ocurrió un error obteniendo los datos del pedido", Toast.LENGTH_SHORT).show()
                (activity as HomeActivity).onBackPressed()
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
                    binding.efectivoLayout.visibility = View.GONE
                    binding.tarjetaLayout.visibility = View.VISIBLE


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
                    binding.tarjetaLayout.visibility = View.GONE
                    binding.efectivoLayout.visibility = View.VISIBLE
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
}