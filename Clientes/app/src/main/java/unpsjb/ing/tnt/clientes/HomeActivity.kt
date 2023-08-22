package unpsjb.ing.tnt.clientes

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
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import unpsjb.ing.tnt.clientes.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    private var _user: FirebaseUser? = null
    private val currentUser get() = _user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLogIn()

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
                R.id.nav_home, R.id.nav_stores, R.id.nav_addresses, R.id.nav_orders, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        findViewById<TextView>(R.id.user_name).text = currentUser?.displayName
        findViewById<TextView>(R.id.user_email).text = currentUser?.email

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, UnauthorizedActivity::class.java))
    }

    fun checkLogIn() {
        _user = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Log.d("HomeActivity", "Usuario no logueado, redireccionando al login...")
            finish()
            startActivity(Intent(this@HomeActivity, UnauthorizedActivity::class.java))
        } else {
            ClientesApplication.loadUsuario(currentUser!!)
        }
    }
}