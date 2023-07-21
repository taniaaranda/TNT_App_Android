package unpsjb.ing.tnt.clientes

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import unpsjb.ing.tnt.clientes.data.model.Carrito
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.Usuario

class ClientesApplication: Application() {
    companion object {
        var carrito: Carrito? = null
        var cargandoStock: Boolean = false
        var usuario: Usuario? = null
        var pedido: Pedido? = null

        fun loadUsuario(firebaseUser: FirebaseUser) {
            if (usuario != null && usuario!!.id != firebaseUser.uid) {
                return
            }

            usuario = Usuario(
                firebaseUser.uid,
                firebaseUser.displayName.toString(),
                firebaseUser.email.toString()
            )
        }

        fun reiniciarPedido() {
            carrito = null
            pedido = null
        }
    }
}