package unpsjb.ing.tnt.clientes.ui.direcciones

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import unpsjb.ing.tnt.clientes.R

class DireccionItemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_direccion_item, container, false)
    }
}