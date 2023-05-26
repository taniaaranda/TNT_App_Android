package unpsjb.ing.tnt.vendedores

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import unpsjb.ing.tnt.vendedores.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private var db: FirebaseFirestore = Firebase.firestore
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    private var _user: FirebaseUser? = null
    private val currentUser get() = _user
    private var tienda: HashMap<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLogIn()
        loadTienda()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_orders, R.id.nav_products, R.id.nav_store, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)

        findViewById<TextView>(R.id.store_id).text = currentUser?.uid
        findViewById<TextView>(R.id.store_email).text = currentUser?.email

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkLogIn() {
        _user = FirebaseAuth.getInstance().currentUser;
        if (currentUser == null) {
            Log.d("HomeActivity", "Usuario no logueado, redireccionando al login...")
            finish()
            startActivity(Intent(this@HomeActivity, UnauthorizedActivity::class.java))
        }
    }

    private fun loadTienda() {
        db.collection("tiendas")
            .whereEqualTo("usuario", currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                tienda = it.documents.first().data as HashMap<String, Any>?
                tienda?.set("id", it.documents.first().id)

                if (!tienda.isNullOrEmpty()) {
                    findViewById<TextView>(R.id.store_id).text = tienda!!.get("id").toString()
                    findViewById<TextView>(R.id.store_email).text = tienda!!.get("nombre").toString()
                } else {
                    Log.d("HomeActivity", "No se cargó la información de la tienda")
                }
            }
            .addOnFailureListener {
                Log.d("HomeActivity", "No se pudo recuperar la tienda del usuario")
            }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, UnauthorizedActivity::class.java))
    }
}