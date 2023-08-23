package unpsjb.ing.tnt.clientes.services

import android.widget.Toast
import androidx.core.R
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificacionesPush : FirebaseMessagingService() {
    val CHANNEL_ID: String = "NOTIFICACIONES"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        FirebaseFirestore.getInstance().collection("notificaciones")
            .whereEqualTo("usuario", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    createToken(token)
                } else {
                    updateToken(it.documents[0].id, token)
                }
            }
    }

    private fun createToken(token: String) {
        FirebaseFirestore.getInstance().collection("notificaciones")
            .add(hashMapOf(
                "usuario" to FirebaseAuth.getInstance().currentUser!!.uid,
                "token" to token
            ))
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Toast.makeText(applicationContext, "Ocurrió un error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateToken(id: String, token: String) {
        FirebaseFirestore.getInstance().collection("notificaciones")
            .document(id)
            .update("token", token)
            .addOnCompleteListener { update ->
                if (!update.isSuccessful) {
                    Toast.makeText(applicationContext, "Ocurrió un error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification
        if (notification != null) {
            val title = notification.title
            val body = notification.body

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.notification_bg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(1, notificationBuilder.build())
        }
    }
}
