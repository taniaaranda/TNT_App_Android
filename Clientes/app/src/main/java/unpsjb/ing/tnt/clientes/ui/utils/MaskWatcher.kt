package unpsjb.ing.tnt.clientes.ui.utils

import android.text.Editable
import android.text.TextWatcher

class MaskWatcher(): TextWatcher {
    private lateinit var mask: String
    private var isRunning: Boolean = false;
    private var isDeleting: Boolean = false;

    constructor(m: String) : this() {
        mask = m
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        isDeleting = count > after
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isRunning || isDeleting || s == null) {
            return
        }

        isRunning = true

        val editableLength = s.length
        if (editableLength <= mask.length && editableLength > 0) {
            val lastCharacter = mask[editableLength - 1]

            if (lastCharacter != '#' && editableLength != 1) {
                s.insert(editableLength - 1, mask, editableLength -1, editableLength)
            }
        } else {
            return
        }

        isRunning = false
    }
}