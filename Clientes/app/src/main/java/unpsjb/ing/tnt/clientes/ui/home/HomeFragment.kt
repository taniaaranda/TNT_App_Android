package unpsjb.ing.tnt.clientes.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.adapter.RubrosAdapter
import unpsjb.ing.tnt.clientes.data.model.Rubro
import unpsjb.ing.tnt.clientes.data.model.Tienda
import unpsjb.ing.tnt.clientes.databinding.FragmentHomeBinding
import unpsjb.ing.tnt.clientes.ui.auth.AuthorizedFragment

class HomeFragment : AuthorizedFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        homeView = binding.root

        setupListeners()

        return homeView
    }

    private fun setupListeners() {
        registerRubroListener()

        binding.busquedaRubro.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("TextChanged", s.toString())
                registerRubroListener(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun registerRubroListener(filter: CharSequence? = null) {
        var rubros = Tienda.getRubrosValues()

        if (filter != null) {
            rubros = rubros.filter {
                it.nombre.contains(filter, true)
            } as ArrayList<Rubro>
        }

        binding.rubros.adapter = RubrosAdapter(this.requireContext(), rubros)
    }
}