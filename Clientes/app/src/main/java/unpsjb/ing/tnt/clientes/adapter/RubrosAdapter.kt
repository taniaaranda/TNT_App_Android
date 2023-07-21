package unpsjb.ing.tnt.clientes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import unpsjb.ing.tnt.clientes.R
import unpsjb.ing.tnt.clientes.data.model.Rubro
import unpsjb.ing.tnt.clientes.databinding.FragmentRubroBinding

class RubrosAdapter(
    private val context: Context,
    private val dataSource: ArrayList<Rubro>
): BaseAdapter() {
    private lateinit var binding: FragmentRubroBinding
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun getCastedItem(position: Int): Rubro {
        val item = getItem(position) as Rubro

        return Rubro(
            item.nombre
        )
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_rubro, parent, false
        )
        binding.rubro = getCastedItem(position)

        binding.nombreRubro.setOnClickListener {
            val bundle = bundleOf("rubro" to getCastedItem(position).nombre)

            parent!!.findNavController().navigate(R.id.nav_stores, bundle)
        }

        return binding.root
    }
}