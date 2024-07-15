package org.dhis2.usescases.biometrics.duplicates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import org.dhis2.R
import org.dhis2.commons.data.SearchTeiModel
import org.dhis2.commons.resources.ColorUtils
import org.dhis2.databinding.ItemSearchTrackedEntityBinding

class BiometricsDuplicatesDialogAdapter(
    private val fm: FragmentManager?,
    private val colorUtils: ColorUtils,
    private val onClickListener: (SearchTeiModel) -> Unit
) : PagingDataAdapter<SearchTeiModel, BiometricsDuplicatesDialogHolder>(object :
    DiffUtil.ItemCallback<SearchTeiModel>() {
    override fun areItemsTheSame(
        oldItem: SearchTeiModel,
        newItem: SearchTeiModel
    ): Boolean {
        return oldItem.tei.uid() == newItem.tei.uid()
    }

    override fun areContentsTheSame(
        oldItem: SearchTeiModel,
        newItem: SearchTeiModel
    ): Boolean {
        return if (oldItem.isOnline && oldItem.tei.state() == null) {
            oldItem.tei.uid() == newItem.tei.uid() &&
                oldItem.tei.state() == null && newItem.tei.state() == null && oldItem.attributeValues == newItem.attributeValues && oldItem.profilePicturePath == newItem.profilePicturePath && oldItem.isAttributeListOpen == newItem.isAttributeListOpen &&
                oldItem.sortingKey == newItem.sortingKey &&
                oldItem.sortingValue == newItem.sortingValue &&
                oldItem.enrolledOrgUnit == newItem.enrolledOrgUnit &&
                oldItem.isBiometricsSearchInProgress == newItem.isBiometricsSearchInProgress
        } else {
            oldItem.tei.uid() == newItem.tei.uid() &&
                oldItem.tei.state() == newItem.tei.state() && oldItem.attributeValues == newItem.attributeValues && oldItem.enrollments == newItem.enrollments && oldItem.profilePicturePath == newItem.profilePicturePath && oldItem.isAttributeListOpen == newItem.isAttributeListOpen &&
                oldItem.sortingKey == newItem.sortingKey &&
                oldItem.sortingValue == newItem.sortingValue &&
                oldItem.enrolledOrgUnit == newItem.enrolledOrgUnit &&
                oldItem.isBiometricsSearchInProgress == newItem.isBiometricsSearchInProgress
        }
    }
}) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BiometricsDuplicatesDialogHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemSearchTrackedEntityBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_search_tracked_entity, parent, false)
        return BiometricsDuplicatesDialogHolder(binding, colorUtils)
    }

    override fun onBindViewHolder(holder: BiometricsDuplicatesDialogHolder, position: Int) {
        holder.bind(
            getItem(position)!!, {
                getItem(holder.adapterPosition)!!.toggleAttributeList()
                notifyItemChanged(holder.adapterPosition)
            },
            { _: String? ->
                // this is failing but BiometricsDuplicatesDialogAdapter doesn't appear from time ago
          /*      if (fm != null) {
                    ImageDetailBottomDialog(
                        null,
                        File(path)
                    ).show(
                        fm,
                        ImageDetailBottomDialog.TAG
                    )
                }*/
            })

        holder.itemView.setOnClickListener { onClickListener(getItem(position)!!) }
    }
}
