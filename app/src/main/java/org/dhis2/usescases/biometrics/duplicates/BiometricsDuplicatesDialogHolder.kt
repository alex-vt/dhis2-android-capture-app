package org.dhis2.usescases.biometrics.duplicates

import androidx.recyclerview.widget.RecyclerView
import org.dhis2.databinding.ItemBiometricsDuplicateBinding

class BiometricsDuplicatesDialogHolder internal constructor(
    private val binding: ItemBiometricsDuplicateBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BiometricsDuplicatesDialogItem, listener: (BiometricsDuplicatesDialogItem) -> Unit) {
        binding.guid.text = item.guid
        binding.executePendingBindings()

        itemView.setOnClickListener { listener(item) }
    }
}
