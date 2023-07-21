package unpsjb.ing.tnt.clientes.data.model

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class Direccion (
    val id: String,
    var calle: String,
    var ubicacion: HashMap<String, Double>,
    var usuario: String
) {
    fun estaGuardado(): Boolean {
        return id.isNotEmpty()
    }

    fun guardar(): Task<out Any> {
        return if (!estaGuardado()) {
            getConnection().add(this.getToSave())
        } else {
            actualizar()
        }
    }

    fun actualizar(): Task<Void> {
        return getConnection().document(id)
            .update(this.getToSave())
    }

    fun getToSave(): HashMap<String, Any> {
        return hashMapOf(
            "calle" to calle,
            "ubicacion" to ubicacion,
            "usuario" to usuario
        )
    }

    companion object {
        const val COLLECTION_NAME = "direcciones"

        fun getConnection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
        }
    }
}