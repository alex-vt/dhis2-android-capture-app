package org.dhis2.data.forms.dataentry.fields.biometrics;

import static org.dhis2.usescases.biometrics.BiometricConstantsKt.BIOMETRICS_FAILURE_PATTERN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dhis2.R;
import org.dhis2.data.biometrics.BiometricsClientFactory;
import org.dhis2.databinding.BiometricsViewBinding;
import org.dhis2.utils.customviews.FieldLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

public class BiometricsView extends FieldLayout {

    private BiometricsViewBinding binding;

    private BiometricsViewModel viewModel;

    LinearLayout biometricsButton;
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
        biometricsButtonText= findViewById(R.id.biometrics_button_text);
        biometricsButtonIcon= findViewById(R.id.biometrics_button_icon);

        biometricsButton.setOnClickListener(v -> registerBiometrics());
    }

    public void setViewModel(BiometricsViewModel viewModel) {
        this.viewModel = viewModel;

        if (binding == null) {
            setLayout();
        }

        String value = viewModel.value();
        Timber.tag("BiometricsView value").d(value);

        if(value !=null && value.length() > 0){
            if(value.equalsIgnoreCase(BIOMETRICS_FAILURE_PATTERN)){
                Timber.tag("BiometricsView").d("onFailure");
                onFailure();
            }else {
                Timber.tag("BiometricsView").d("onSuccess");
                onSuccess();
            }
        }else {
            Timber.tag("BiometricsView").d("onInitial");
        }
    }

    void onSuccess(){
        biometricsButton.setBackgroundColor(ContextCompat.getColor(
                rootView.getContext(),
                R.color.green_success
        ));

        biometricsButtonText.setText(R.string.biometrics_completed);

        biometricsButtonIcon.setImageDrawable(
                AppCompatResources.getDrawable(rootView.getContext(), R.drawable.ic_biometrics_success));
    }

    void onFailure(){
                biometricsButton.setBackgroundColor(ContextCompat.getColor(
                        rootView.getContext(),
                R.color.warning_dark_color
        ));

        biometricsButtonText.setText(R.string.biometrics_declined);

        biometricsButtonIcon.setImageDrawable(
                AppCompatResources.getDrawable(rootView.getContext(), R.drawable.ic_biometrics_warning));
    }

    private void registerBiometrics() {
        BiometricsClientFactory.INSTANCE.get(rootView.getContext()).register((Activity)rootView.getContext());
    }
}
