package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import org.dhis2.R
import org.dhis2.bindings.app
import org.dhis2.databinding.DialogSelectUpgBinding
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.SelectUPGDialogModule
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGItem
import javax.inject.Inject

class SelectUPGDialog : DialogFragment(), SelectUPGDialogView {

    private var onSelectUPGListener: ((upgItem: UPGItem) -> Unit)? = null
    private lateinit var binding: DialogSelectUpgBinding

    @Inject
    lateinit var presenter: SelectUPGDialogPresenter

    private lateinit var adapter: SelectUPGDialogAdapter

    private var isDialogShown = false
        private set

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        create()
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_select_upg,
            container,
            false
        )

        val orgUnitUId: String = requireArguments().getString(ORG_UNIT_UID)!!

        binding.cancelButton.setOnClickListener { this.dismiss() }

        this.adapter =
            SelectUPGDialogAdapter { item: UPGItem ->

                presenter.onItemClick(item)
            }

        binding.upgRecycler.adapter = adapter

        presenter.init(this, orgUnitUId)

        isDialogShown = true

        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        presenter.onDetach()
        super.onCancel(dialog)
    }

    override fun renderList(upgItems: List<UPGItem>) {
        adapter.setItems(upgItems)
    }

    override fun onUPGSelected(item: UPGItem) {
        onSelectUPGListener?.invoke(item)
        dismiss()
    }

    override fun dismiss() {
        presenter.onDetach()
        if (isDialogShown) {
            isDialogShown = false
            super.dismiss()
        }
    }


    fun setUPGSelectedListener(onSelectUPGListener: (item: UPGItem) -> Unit) {
        this.onSelectUPGListener = onSelectUPGListener
    }

    private fun create() {
        app()
            .userComponent()!!
            .plus(SelectUPGDialogModule())
            .inject(this)
    }

    companion object {
        private const val ORG_UNIT_UID = "ORG_UNIT_UID"

        val TAG: String = this::class.java.name

        @JvmStatic
        fun newInstance(
            orgUnitUId: String,
        ): SelectUPGDialog {
            val fragment = SelectUPGDialog()

            val args = Bundle()
            args.putString(ORG_UNIT_UID, orgUnitUId)

            fragment.arguments = args

            return fragment
        }
    }
}