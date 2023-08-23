package unpsjb.ing.tnt.clientes

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.google.firebase.auth.FirebaseUser
import unpsjb.ing.tnt.clientes.data.model.Carrito
import unpsjb.ing.tnt.clientes.data.model.Pedido
import unpsjb.ing.tnt.clientes.data.model.Usuario
import unpsjb.ing.tnt.clientes.services.NotificacionesPush

class ClientesApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        configurarNotificaciones()
    }

    private fun configurarNotificaciones() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(NotificacionesPush::CHANNEL_ID.toString(), name, importance)
        mChannel.description = descriptionText

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

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