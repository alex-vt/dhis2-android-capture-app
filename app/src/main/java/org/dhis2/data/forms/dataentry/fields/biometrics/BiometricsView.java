package org.dhis2.data.forms.dataentry.fields.biometrics;

import static org.dhis2.utils.Constants.SIMPRINTS_ENROLL_REQUEST;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.dhis2.R;
import org.dhis2.databinding.BiometricsViewBinding;
import org.dhis2.databinding.LogoDhisBindingImpl;
import org.dhis2.utils.Constants;
import org.dhis2.utils.customviews.FieldLayout;
import org.dhis2.utils.simprints.SimprintsHelper;

import java.util.List;

import timber.log.Timber;

public class BiometricsView extends FieldLayout {

    private BiometricsViewBinding binding;

    private BiometricsViewModel viewModel;

    Button biometricsButton;
    Button biometricsStatus;
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
        binding.inflate(inflater, this, true);

        biometricsButton = findViewById(R.id.biometricsButton);
        biometricsStatus = findViewById(R.id.biometricsStatus);
        rootView = findViewById(R.id.rootView);

        biometricsButton.setOnClickListener(v -> launchSimprints());
    }

    public void setViewModel(BiometricsViewModel viewModel) {
        this.viewModel = viewModel;

        if (binding == null) {
            setLayout();
        }

        String value = viewModel.value();
        Timber.tag("BiometricsView value").d(value);

        if(value !=null && value.length() > 0){
            if(value.equalsIgnoreCase(Constants.BIOMETRICS_FAILURE_PATTERN)){
                Timber.tag("BiometricsView").d("onFailure");
                onFailure();
            }else {
                Timber.tag("BiometricsView").d("onSuccess");
                onSuccess();
            }
        }else {
            Timber.tag("BiometricsView").d("onInitial");
            onInitial();
        }
    }

    void onInitial(){
        biometricsStatus.setVisibility(View.GONE);

        biometricsButton.setVisibility(VISIBLE);
    }

    void onSuccess(){
        biometricsStatus.setBackgroundColor(rootView.getContext().getResources().getColor(R.color.green_7ed));
        biometricsStatus.setText("BIOMETRICS COMPLETED");
        biometricsStatus.setVisibility(VISIBLE);

        biometricsButton.setVisibility(View.GONE);
    }

    void onFailure(){
        biometricsStatus.setBackgroundColor(rootView.getContext().getResources().getColor(R.color.red_060));
        biometricsStatus.setText("BIOMETRICS DECLINED");
        biometricsStatus.setVisibility(VISIBLE);

        biometricsButton.setText("TRY AGAIN");
        biometricsButton.setBackgroundColor(rootView.getContext().getResources().getColor(R.color.gray_979));
    }

    private void launchSimprints() {
        //Launch Simprints App Intent - ProjectId, UserId, ModuleId.
        Intent intent = SimprintsHelper.simHelper.register("Module ID");


        PackageManager manager = rootView.getContext().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            ((Activity)rootView.getContext()).startActivityForResult(intent, SIMPRINTS_ENROLL_REQUEST);
        } else {
            Toast.makeText(rootView.getContext(), "Please download simprints app!", Toast.LENGTH_SHORT).show();
        }
    }
}