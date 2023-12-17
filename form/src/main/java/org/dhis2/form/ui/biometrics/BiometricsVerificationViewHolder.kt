package org.dhis2.form.ui.biometrics

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ViewDataBinding
import org.dhis2.form.R
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.biometrics.BiometricsDataElementStatus
import org.dhis2.form.model.biometrics.BiometricsDataElementUiModelImpl
import org.dhis2.form.ui.FormViewHolder
import org.dhis2.form.ui.event.RecyclerViewUiEvents
import org.dhis2.form.ui.intent.FormIntent

class BiometricsVerificationViewHolder(private val binding: ViewDataBinding) : FormViewHolder(binding) {
    private val verificationContainer:LinearLayout = binding.root.findViewById(R.id.verification_container)
    private val biometricsButton:LinearLayout = binding.root.findViewById(R.id.biometrics_button)

    private val tryAgainButton:LinearLayout = binding.root.findViewById(R.id.tryAgainButton)
    private val statusImageView:ImageView = binding.root.findViewById(R.id.statusImageView)

    fun bind(
        uiModel: FieldUiModel,
        callback: FieldItemCallback,
    ) {
        val itemCallback: FieldUiModel.Callback = object : FieldUiModel.Callback {
            override fun recyclerViewUiEvents(uiEvent: RecyclerViewUiEvents) {
                callback.recyclerViewEvent(uiEvent)
            }

            override fun intent(intent: FormIntent) {
                var formIntent = intent
                if (intent is FormIntent.OnNext) {
                    formIntent = intent.copy(
                        position = layoutPosition
                    )
                }
                callback.intent(formIntent)
            }
        }
        uiModel.setCallback(itemCallback)

        val uiVerificationUiModel = (uiModel as BiometricsDataElementUiModelImpl)

        if (!uiVerificationUiModel.value.isNullOrEmpty()){
            renderVerification(uiVerificationUiModel)
        } else {
            renderRegistration(uiVerificationUiModel)
        }

    }

    private fun renderVerification(uiModel : BiometricsDataElementUiModelImpl){
        tryAgainButton.setOnClickListener {
            (uiModel as BiometricsDataElementUiModelImpl).onRetryVerificationClick()
        }

        verificationContainer.visibility = VISIBLE
        biometricsButton.visibility = GONE

        when (uiModel.status) {
            BiometricsDataElementStatus.SUCCESS -> {
                statusImageView.visibility = VISIBLE
                statusImageView.setImageDrawable(AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_bio_available_yes));
                tryAgainButton.visibility = GONE
            }
            BiometricsDataElementStatus.FAILURE -> {
                statusImageView.visibility = VISIBLE
                statusImageView.setImageDrawable(AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_bio_available_no));
                tryAgainButton.visibility = VISIBLE
            }
            else -> {
                statusImageView.visibility = GONE
                tryAgainButton.visibility = GONE
            }
        }
    }

    private fun renderRegistration(uiModel : BiometricsDataElementUiModelImpl){
        verificationContainer.visibility = GONE
        biometricsButton.visibility = VISIBLE

        biometricsButton.setOnClickListener {
            uiModel.onBiometricClick()
        }
    }
}