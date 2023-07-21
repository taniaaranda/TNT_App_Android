package unpsjb.ing.tnt.clientes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UnauthorizedActivity : AppCompatActivity() {
    private var db: FirebaseFirestore = Firebase.firestore
    var _user: FirebaseUser? = null
    private val currentUser get() = _user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unauthorized)
        checkUser()
    }

    private fun checkUser() {
        _user = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            ClientesApplication.loadUsuario(currentUser!!)

            Log.d("UnauthorizedActivity", "Usuario: " + currentUser!!.uid)
            db.collection("tiendas").whereEqualTo("usuario", currentUser!!.uid).get()
                .addOnSuccessListener {
                    Log.d("UnauthorizedActivity", "Usuario logueado, redireccionando...")
                    finish()
                    startActivity(Intent(this@UnauthorizedActivity, HomeActivity::class.java))
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
                    checkUser()
                } else {
                    Toast.makeText(applicationContext, "¡Usuario o clave inválidos!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}