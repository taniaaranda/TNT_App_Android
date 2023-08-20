package unpsjb.ing.tnt.vendedores

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import unpsjb.ing.tnt.vendedores.data.model.Tienda


class UnauthorizedActivity : AppCompatActivity() {
    private var db: FirebaseFirestore = Firebase.firestore
    var _user: FirebaseUser? = null
    private val currentUser get() = _user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unauthorized)
        _user = FirebaseAuth.getInstance().currentUser
        checkUser()
    }

    private fun checkUser() {
        if (currentUser != null) {
            Log.d("UnauthorizedActivity", "Usuario: " + currentUser!!.uid)
            db.collection("tiendas").whereEqualTo("usuario", currentUser!!.uid).get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        Log.d("UnauthorizedActivity", "El usuario no tiene una tienda, redireccionando...")
                        finish()
                        startActivity(Intent(this@UnauthorizedActivity, NuevaTiendaActivity::class.java))
                    } else {
                        Log.d("UnauthorizedActivity", "Usuario logueado y con una tienda creada, redireccionando...")
                        val tienda = it.first()

                        VendedoresApplication.tienda = Tienda(
                            tienda.id,
                            tienda.get("nombre").toString(),
                            tienda.get("rubro").toString(),
                            tienda.get("calle").toString(),
                            tienda.get("ubicacionLatLong") as HashMap<String, Double>,
                            tienda.get("horario_de_atencion") as HashMap<String, String>,
                            tienda.get("metodos_de_pago") as ArrayList<String>
                        )

                        finish()
                        startActivity(Intent(this@UnauthorizedActivity, HomeActivity::class.java))
                    }
                }
                .addOnFailureListener {
                    Log.d("GetStoreError", it.message.toString())
                }
        }
    }

    fun logIn(email: String, clave: String) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, clave)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _user = FirebaseAuth.getInstance().currentUser
                    checkUser()
                } else {
                    Toast.makeText(baseContext, "¡Usuario o clave inválidos!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}