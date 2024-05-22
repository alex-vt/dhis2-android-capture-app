package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.ui

import androidx.recyclerview.widget.RecyclerView
import org.dhis2.databinding.ItemUpgBinding
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGItem

class SelectUPGDialogHolder(
    private val binding: ItemUpgBinding
) : RecyclerView.ViewHolder(binding.root) {

    lateinit var item: UPGItem

    fun bind(
        item: UPGItem
    ) {
        this.item = item
        binding.item = item
    }
}
