package unpsjb.ing.tnt.clientes.data.model

import com.google.firebase.firestore.FirebaseFirestore

class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val direcciones: ArrayList<Direccion>
) {
    fun cargarDirecciones() {
        FirebaseFirestore.getInstance().collection(Direccion.COLLECTION_NAME)
            .whereEqualTo("usuario", id)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    for (document in snapshots.documents) {
                        direcciones.add(Direccion(
                            document.id,
                            document.get("calle") as String,
                            document.get("ubicacion") as HashMap<String, Double>,
                            id
                        ))
                    }
                }
            }
    }
}