package org.dhis2.form.ui.biometrics

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import org.dhis2.commons.biometrics.BIOMETRICS_FAILURE_PATTERN
import org.dhis2.commons.biometrics.*
import org.dhis2.form.R
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.biometrics.BiometricsAttributeUiModelImpl
import org.dhis2.form.ui.FormViewHolder
import org.dhis2.form.ui.event.RecyclerViewUiEvents
import org.dhis2.form.ui.intent.FormIntent
import timber.log.Timber

class BiometricsAttributeViewHolder(private val binding: ViewDataBinding) : FormViewHolder(binding) {
    private val biometricsButton: LinearLayout = binding.root.findViewById(R.id.biometrics_button)
    private val retakeButton: LinearLayout = binding.root.findViewById(R.id.biometrics_retake_button)
    private val biometricsButtonText : TextView = binding.root.findViewById(R.id.biometrics_button_text)
    private val biometricsButtonIcon:ImageView = binding.root.findViewById(R.id.biometrics_button_icon)

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

        biometricsButton.setOnClickListener {
            (uiModel as BiometricsAttributeUiModelImpl).onBiometricsClick()
        }

        retakeButton.setOnClickListener {
            (uiModel as BiometricsAttributeUiModelImpl).onBiometricsClick()
        }

        val value = uiModel.value
        Timber.tag("BiometricsView value").d(value);

        if (value != null && value.isNotEmpty()) {
            if (value.startsWith(BIOMETRICS_FAILURE_PATTERN)) {
                Timber.tag("BiometricsView").d("onFailure");
                onFailure();
            } else {
                Timber.tag("BiometricsView").d("onSuccess");
                onSuccess();
            }
        } else {
            onInitial();
            Timber.tag("BiometricsView").d("onInitial");
        }
    }

    private fun onInitial() {
        retakeButton.visibility = View.GONE;
        biometricsButton.isEnabled = true;
        biometricsButtonIcon.setImageDrawable(
            AppCompatResources.getDrawable(binding.root.context,
                binding.root.context.getBioIconNew()))
    }

    private fun onSuccess() {
        retakeButton.visibility = View.VISIBLE;
        biometricsButton.background = ContextCompat.getDrawable(
            binding.root.context,
            R.drawable.button_round_success
        );

        biometricsButton.isEnabled = false;

        biometricsButtonText.setText(R.string.biometrics_completed);

        biometricsButtonIcon.setImageDrawable(
            AppCompatResources.getDrawable(binding.root.context,
                binding.root.context.getBioIconSuccess()))
    }

    private fun onFailure() {
        retakeButton.visibility = View.VISIBLE;
        biometricsButton.background = ContextCompat.getDrawable(
            binding.root.context,
            R.drawable.button_round_warning
        );

        biometricsButton.isEnabled = false;

        biometricsButtonText.setText(R.string.biometrics_declined);

        biometricsButtonIcon.setImageDrawable(
            AppCompatResources.getDrawable(binding.root.context,
                binding.root.context.getBioIconWarning()))
    }
}