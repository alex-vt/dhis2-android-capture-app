package org.dhis2.data.forms.dataentry.fields.biometrics;

import static org.dhis2.usescases.biometrics.BiometricConstantsKt.BIOMETRICS_FAILURE_PATTERN;
import static org.dhis2.usescases.biometrics.ExtensionsKt.getBioIconNew;
import static org.dhis2.usescases.biometrics.ExtensionsKt.getBioIconBasic;
import static org.dhis2.usescases.biometrics.ExtensionsKt.getBioIconNew;
import static org.dhis2.usescases.biometrics.ExtensionsKt.getBioIconSuccess;
import static org.dhis2.usescases.biometrics.ExtensionsKt.getBioIconWarning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dhis2.R;
import org.dhis2.databinding.BiometricsViewBinding;
import org.dhis2.utils.customviews.FieldLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

//TODO: simprints
/*public class BiometricsView extends FieldLayout {

    private BiometricsViewBinding binding;

    private BiometricsViewModel viewModel;

    LinearLayout biometricsButton;
    LinearLayout retakeButton;
    TextView biometricsButtonText;
    ImageView biometricsButtonIcon;
    LinearLayout rootView;

    public BiometricsView(Context context) {
        super(context);
        init(context);
    }

    public BiometricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BiometricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        super.init(context);
        inflater = LayoutInflater.from(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setLayout() {
        binding = BiometricsViewBinding.inflate(inflater, this, true);

        rootView = findViewById(R.id.rootView);
        biometricsButton = findViewById(R.id.biometrics_button);
        retakeButton = findViewById(R.id.biometrics_retake_button);
        biometricsButtonText = findViewById(R.id.biometrics_button_text);
        biometricsButtonIcon = findViewById(R.id.biometrics_button_icon);

        biometricsButton.setOnClickListener(v -> {
            if (viewModel != null) {
                viewModel.onBiometricsClick();
            }
        });

        retakeButton.setOnClickListener(v -> {
            if (viewModel != null) {
                viewModel.onBiometricsClick();
            }
        });
    }

    public void setViewModel(BiometricsViewModel viewModel) {
        this.viewModel = viewModel;

        if (binding == null) {
            setLayout();
        }

        String value = viewModel.value();
        Timber.tag("BiometricsView value").d(value);

        if (value != null && value.length() > 0) {
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

    void onInitial() {
        retakeButton.setVisibility(View.GONE);
        biometricsButton.setEnabled(true);
        biometricsButtonIcon.setImageDrawable(
                AppCompatResources.getDrawable(rootView.getContext(),
                        getBioIconNew(rootView.getContext())));
    }

    void onSuccess() {
        retakeButton.setVisibility(View.VISIBLE);
        biometricsButton.setBackground(ContextCompat.getDrawable(
                rootView.getContext(),
                R.drawable.button_round_success
        ));

        biometricsButton.setEnabled(false);

        biometricsButtonText.setText(R.string.biometrics_completed);

        biometricsButtonIcon.setImageDrawable(
                AppCompatResources.getDrawable(rootView.getContext(),
                        getBioIconSuccess(rootView.getContext())));
    }

    void onFailure() {
        retakeButton.setVisibility(View.VISIBLE);
        biometricsButton.setBackground(ContextCompat.getDrawable(
                rootView.getContext(),
                R.drawable.button_round_warning
        ));

        biometricsButton.setEnabled(false);

        biometricsButtonText.setText(R.string.biometrics_declined);

        biometricsButtonIcon.setImageDrawable(
                AppCompatResources.getDrawable(rootView.getContext(),
                        getBioIconWarning(rootView.getContext())));
    }
}*/
