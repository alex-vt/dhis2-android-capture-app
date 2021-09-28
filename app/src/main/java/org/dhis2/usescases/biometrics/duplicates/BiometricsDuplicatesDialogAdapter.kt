package org.dhis2.usescases.biometrics.duplicates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import org.dhis2.R
import org.dhis2.databinding.ItemBiometricsDuplicateBinding

class BiometricsDuplicatesDialogAdapter(
    private val items: List<BiometricsDuplicatesDialogItem>,
    private val clickListener: (BiometricsDuplicatesDialogItem) -> Unit
) : RecyclerView.Adapter<BiometricsDuplicatesDialogHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BiometricsDuplicatesDialogHolder {
        val binding =
            DataBindingUtil.inflate<ItemBiometricsDuplicateBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_biometrics_duplicate,
                parent,
                false
            )
        return BiometricsDuplicatesDialogHolder(binding)
    }

    override fun onBindViewHolder(holder: BiometricsDuplicatesDialogHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    override fun getItemCount() = items.size
}
