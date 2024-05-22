package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import org.dhis2.R
import org.dhis2.databinding.ItemUpgBinding
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGItem

class SelectUPGDialogAdapter(
    private val onClickListener: (UPGItem) -> Unit
) : RecyclerView.Adapter<SelectUPGDialogHolder>() {

    private var items: List<UPGItem> = emptyList()
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SelectUPGDialogHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding: ItemUpgBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_upg, viewGroup, false)
        return SelectUPGDialogHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectUPGDialogHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener { onClickListener(getItem(position)) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): UPGItem {
        return items[position]
    }

    fun setItems(items: List<UPGItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}
