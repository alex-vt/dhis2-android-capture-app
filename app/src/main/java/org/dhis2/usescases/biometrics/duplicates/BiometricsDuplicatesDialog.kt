package org.dhis2.usescases.biometrics.duplicates

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import javax.inject.Inject
import org.dhis2.Bindings.app
import org.dhis2.R
import org.dhis2.databinding.DialogBiometricsDuplicatesBinding

class BiometricsDuplicatesDialog() : DialogFragment(), BiometricsDuplicatesDialogView {

    private lateinit var binding: DialogBiometricsDuplicatesBinding

    @Inject
    lateinit var presenter: BiometricsDuplicatesDialogPresenter

    private lateinit var adapter: BiometricsDuplicatesDialogAdapter

    var isDialogShown = false
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
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_biometrics_duplicates, container, false)

        val guids:List<String> = requireArguments().getString(GUIDs)!!.split(",")
        val sessionId = requireArguments().getString(SESSION_ID)!!

        binding.enrollNewButton.setOnClickListener { view ->
           // clearListener?.onClick(view)
            this.dismiss()
        }
        binding.cancelButton.setOnClickListener { this.dismiss() }

        presenter.init(this, guids, sessionId)

        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        presenter.onDetach()
        super.onCancel(dialog)
    }

    override fun setData(data: List<BiometricsDuplicatesDialogItem>) {
        binding.duplicatesRecycler.adapter = BiometricsDuplicatesDialogAdapter(data) {
            //onItemSelected(it.uid)
            this.dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        isDialogShown = true
        super.show(manager, tag)
    }

    override fun dismiss() {
        presenter.onDetach()
        if (isDialogShown) {
            isDialogShown = false
            super.dismiss()
        }
    }

    private fun create() {
        app()
            .serverComponent()!!
            .plus(BiometricsDuplicatesDialogModule())
            .inject(this)
    }

    companion object {
        private const val GUIDs = "GUIDs"
        private const val SESSION_ID = "sessionId"

        val TAG: String = this::class.java.name

        @JvmStatic
        fun newInstance(guids: List<String>, sessionId: String): BiometricsDuplicatesDialog {
            val fragment = BiometricsDuplicatesDialog()

            val args = Bundle()
            args.putString(GUIDs,guids.joinToString ())
            args.putString(SESSION_ID, sessionId)
            fragment.arguments = args

            return fragment
        }
    }
}
