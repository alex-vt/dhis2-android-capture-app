package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment;

import static android.app.Activity.RESULT_OK;

import static org.dhis2.usescases.biometrics.BiometricConstantsKt.BIOMETRICS_GUID;
import static org.dhis2.usescases.biometrics.BiometricConstantsKt.BIOMETRICS_TEI_ORGANISATION_UNIT;
import static org.dhis2.usescases.biometrics.BiometricConstantsKt.BIOMETRICS_VERIFICATION_STATUS;
import static org.dhis2.usescases.biometrics.BiometricConstantsKt.BIOMETRICS_VERIFY_REQUEST;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.dhis2.Bindings.ViewExtensionsKt;
import org.dhis2.R;
import org.dhis2.data.biometrics.BiometricsClientFactory;
import org.dhis2.data.biometrics.VerifyResult;
import org.dhis2.data.forms.dataentry.FormView;
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationView;
import org.dhis2.data.location.LocationProvider;
import org.dhis2.databinding.SectionSelectorFragmentBinding;
import org.dhis2.form.data.FormRepository;
import org.dhis2.form.model.FieldUiModel;
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.EventCaptureActivity;
import org.dhis2.usescases.general.FragmentGlobalAbstract;
import org.dhis2.utils.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import kotlin.Unit;

public class EventCaptureFormFragment extends FragmentGlobalAbstract implements EventCaptureFormView {

    @Inject
    EventCaptureFormPresenter presenter;

    @Inject
    FormRepository formRepository;

    @Inject
    LocationProvider locationProvider;

    private EventCaptureActivity activity;
    private SectionSelectorFragmentBinding binding;
    private FormView formView;

    private String biometricsGuid;
    private int biometricsVerificationStatus;
    private String teiOrgUnit;

    public static EventCaptureFormFragment newInstance(String eventUid) {
        EventCaptureFormFragment fragment = new EventCaptureFormFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EVENT_UID, eventUid);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventCaptureFormFragment newInstance(String eventUid, String guid, int status, String teiOrgUnit) {
        EventCaptureFormFragment fragment = new EventCaptureFormFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EVENT_UID, eventUid);
        args.putString(BIOMETRICS_GUID, guid);
        args.putInt(BIOMETRICS_VERIFICATION_STATUS, status);
        args.putString(BIOMETRICS_TEI_ORGANISATION_UNIT, teiOrgUnit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.activity = (EventCaptureActivity) context;

        biometricsGuid = getArguments().getString(BIOMETRICS_GUID);
        biometricsVerificationStatus = getArguments().getInt(BIOMETRICS_VERIFICATION_STATUS);
        teiOrgUnit = BIOMETRICS_TEI_ORGANISATION_UNIT;

        activity.eventCaptureComponent.plus(
                new EventCaptureFormModule(
                        this,
                        getArguments().getString(Constants.EVENT_UID))
        ).inject(this);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        formView = new FormView.Builder()
                .persistence(formRepository)
                .locationProvider(locationProvider)
                .onItemChangeListener(action -> {
                    activity.getPresenter().setValueChanged(action.getId());
                    activity.getPresenter().nextCalculation(true);
                    return Unit.INSTANCE;
                })
                .onLoadingListener(loading -> {
                    if(loading){
                        activity.showProgress();
                    } else{
                        activity.hideProgress();
                    }
                    return Unit.INSTANCE;
                })
                .factory(activity.getSupportFragmentManager())
                .build();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.section_selector_fragment, container, false);
        binding.setPresenter(activity.getPresenter());
        binding.actionButton.setOnClickListener(view -> {
            ViewExtensionsKt.closeKeyboard(view);
            performSaveClick();
        });

        presenter.initBiometricsValues(biometricsGuid, biometricsVerificationStatus, teiOrgUnit);

        presenter.init();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.formViewContainer, formView).commit();
        formView.setScrollCallback(isSectionVisible -> {
            animateFabButton(isSectionVisible);
            return Unit.INSTANCE;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case BIOMETRICS_VERIFY_REQUEST:
                if (resultCode == RESULT_OK) {
                    VerifyResult result = BiometricsClientFactory.INSTANCE.get(
                            this.getContext()).handleVerifyResponse(data);

                    if (result instanceof VerifyResult.Match) {
                        presenter.refreshBiometricsVerificationStatus(1);
                    } else if (result instanceof VerifyResult.NoMatch) {
                        presenter.refreshBiometricsVerificationStatus(0);
                    } else if (result instanceof VerifyResult.Failure) {
                        presenter.refreshBiometricsVerificationStatus(0);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showFields(@NonNull List<FieldUiModel> updates) {
        formView.render(updates);
    }

    private void animateFabButton(boolean sectionIsVisible) {
        int translationX = 1000;
        if (sectionIsVisible) translationX = 0;

        binding.actionButton.animate().translationX(translationX).setDuration(500).start();
    }

    @Override
    public void performSaveClick() {
        if (activity.getCurrentFocus() instanceof EditText) {
            presenter.setFinishing();
            activity.getCurrentFocus().clearFocus();
        } else {
            presenter.onActionButtonClick();
        }
    }

    @Override
    public void verifyBiometrics(@Nullable String biometricsGuid, @Nullable String teiOrgUnit) {

        BiometricsClientFactory.INSTANCE.get(this.getContext()).verify(this, biometricsGuid, teiOrgUnit);
    }
}