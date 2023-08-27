package unpsjb.ing.tnt.vendedores.data.model

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable

class Tienda(
    val id: String,
    val nombre: String,
    val rubro: String,
    val calle: String,
    val ubicacion: HashMap<String, Double>,
    val horarioDeAtencion: HashMap<String, String>,
    val metodosDePago: ArrayList<String>
) {
    companion object {
        private val RUBROS = arrayListOf(
            Rubro("Todos"),
            Rubro("Despensa"),
            Rubro("Ferretería"),
            Rubro("Mercería"),
            Rubro("Heladería"),
            Rubro("Rotisería")
        )

        fun getRubrosValues(): ArrayList<Rubro> {
            return RUBROS
        }

        fun hidratar(documentSnapshot: DocumentSnapshot): Tienda {
            return Tienda(
                documentSnapshot.id,
                documentSnapshot.get("nombre").toString(),
                documentSnapshot.get("rubro").toString(),
                documentSnapshot.get("calle").toString(),
                documentSnapshot.get("ubicacion") as HashMap<String, Double>,
                documentSnapshot.get("horario_de_atencion") as HashMap<String, String>,
                documentSnapshot.get("metodos_de_pago") as ArrayList<String>,
            )
        }
    }
}