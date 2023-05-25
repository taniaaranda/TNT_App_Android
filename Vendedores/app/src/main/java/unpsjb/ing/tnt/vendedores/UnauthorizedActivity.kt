package unpsjb.ing.tnt.vendedores

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class UnauthorizedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unauthorized)

        if (FirebaseAuth.getInstance().currentUser != null) {
            Log.d("UnauthorizedActivity", "User is logged in, redirecting...")
            finish()
            startActivity(Intent(this@UnauthorizedActivity, HomeActivity::class.java))
        }
    }

    fun logIn(email: String, clave: String) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, clave)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    finish()
                    startActivity(Intent(this@UnauthorizedActivity, HomeActivity::class.java))
                } else {
                    AlertDialog.Builder(applicationContext).apply{
                        setTitle("¡Usuario o contraeña incorrectos!")
                    }.show()
                }
            }
    }
}