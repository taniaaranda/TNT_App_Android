package unpsjb.ing.tnt.vendedores

import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class FirebaseConnectedFragment : Fragment() {
    private var dbReference: FirebaseFirestore = Firebase.firestore

    fun getDbReference(): FirebaseFirestore {
        return dbReference
    }
}