package org.dhis2.form.ui.biometrics

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ViewDataBinding
import org.dhis2.form.R
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.biometrics.BiometricsVerificationStatus
import org.dhis2.form.model.biometrics.BiometricsVerificationUiModelImpl
import org.dhis2.form.ui.FormViewHolder
import org.dhis2.form.ui.event.RecyclerViewUiEvents
import org.dhis2.form.ui.intent.FormIntent

class BiometricsVerificationViewHolder(private val binding: ViewDataBinding) : FormViewHolder(binding) {
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


        tryAgainButton.setOnClickListener {
            (uiModel as BiometricsVerificationUiModelImpl).onRetryVerificationClick()
        }

        when ((uiModel as BiometricsVerificationUiModelImpl).status) {
            BiometricsVerificationStatus.SUCCESS -> {
                statusImageView.visibility = VISIBLE
                statusImageView.setImageDrawable(AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_bio_available_yes));
                tryAgainButton.visibility = GONE
            }
            BiometricsVerificationStatus.FAILURE -> {
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
}