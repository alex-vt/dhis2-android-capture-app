package org.dhis2.data.forms.dataentry.fields.biometricsVerification;

import static org.dhis2.utils.Constants.SIMPRINTS_VERIFY_REQUEST;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.dhis2.R;
import org.dhis2.databinding.BiometricsVerificationViewBinding;
import org.dhis2.utils.customviews.FieldLayout;
import org.dhis2.utils.simprints.SimprintsHelper;

import java.util.List;

import androidx.appcompat.content.res.AppCompatResources;
import timber.log.Timber;

public class BiometricsVerificationView extends FieldLayout {

    private BiometricsVerificationViewBinding binding;

    private BiometricsVerificationViewModel viewModel;

    public enum BiometricsVerificationStatus {
        NOT_DONE,
        SUCCESS,
        FAILURE
    }

    private String guid = null;

    Button tryAgainButton;
    ImageView statusImageView;

    public BiometricsVerificationView(Context context) {
        super(context);
        init(context);
    }

    public BiometricsVerificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BiometricsVerificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        super.init(context);
        inflater = LayoutInflater.from(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setLayout() {
        binding.inflate(inflater, this, true);

        tryAgainButton = findViewById(R.id.tryAgainButton);
        statusImageView  = findViewById(R.id.statusImageView);

        tryAgainButton.setOnClickListener(v ->  launchSimprintsForVerification());
    }

    public void setViewModel(BiometricsVerificationViewModel viewModel) {
        this.viewModel = viewModel;

        if (binding == null) {
            setLayout();
        }

        guid = viewModel.value();

        if(viewModel.status().equals(BiometricsVerificationStatus.SUCCESS)){
            statusImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_biometrics_available_yes_24dp));
            tryAgainButton.setVisibility(GONE);
        }else if(viewModel.status().equals(BiometricsVerificationStatus.FAILURE)){
            statusImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_biometrics_available_no_24dp));
            tryAgainButton.setVisibility(VISIBLE);
        }else{
            statusImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_biometrics_available_no_24dp));
            tryAgainButton.setVisibility(GONE);
        }
    }


    private void launchSimprintsForVerification() {
        //Launch Simprints App Intent - ProjectId, UserId, ModuleId.
        if(guid ==  null){
            Timber.i("Simprints Verification - Guid is Null - Please check again!");
            return;
        }
        Intent simIntent = SimprintsHelper.simHelper.verify("MODULE ID", guid);

        PackageManager manager = this.getContext().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(simIntent, 0);
        if (infos.size() > 0) {
            ((Activity)this.getContext()).startActivityForResult(simIntent, SIMPRINTS_VERIFY_REQUEST);
        } else {
            Toast.makeText(this.getContext(), "Please download simprints app!", Toast.LENGTH_SHORT).show();

        }
    }
}
