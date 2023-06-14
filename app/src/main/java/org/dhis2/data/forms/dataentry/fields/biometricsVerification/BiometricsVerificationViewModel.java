package org.dhis2.data.forms.dataentry.fields.biometricsVerification;
//TODO: simprints
/*
import com.google.auto.value.AutoValue;

import org.dhis2.R;
import org.dhis2.data.forms.dataentry.DataEntryViewHolderTypes;
import org.dhis2.data.forms.dataentry.fields.FieldViewModel;
import org.dhis2.data.forms.dataentry.fields.biometrics.BiometricsViewModel;
import org.dhis2.form.model.RowAction;
import org.dhis2.form.ui.style.FormUiModelStyle;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ValueType;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import io.reactivex.processors.FlowableProcessor;

@AutoValue
public abstract class BiometricsVerificationViewModel extends FieldViewModel {

    @NonNull
    public abstract BiometricsVerificationView.BiometricsVerificationStatus status();

    public static FieldViewModel create(String id, int layoutId, String label, Boolean mandatory, String value, String section, Boolean editable, String description, ObjectStyle objectStyle, FormUiModelStyle style,String url, BiometricsVerificationView.BiometricsVerificationStatus status) {
        return new AutoValue_BiometricsVerificationViewModel(id, layoutId, label, mandatory, value, section, null, editable, null, null, null, description, objectStyle, null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style,null, false, ValueType.TEXT, url, status);
    }

    @Override
    public FieldViewModel setMandatory() {
        return new AutoValue_BiometricsVerificationViewModel(uid(), layoutId(), label(), true,value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style(), null, activated(), ValueType.TEXT, url(), status());
    }

    @NonNull
    @Override
    public FieldViewModel withError(@NonNull String error) {
        return new AutoValue_BiometricsVerificationViewModel(uid(), layoutId(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error, description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style(), null, activated(), ValueType.TEXT, url(), status());
    }

    @NonNull
    @Override
    public FieldViewModel withWarning(@NonNull String warning) {
        return new AutoValue_BiometricsVerificationViewModel(uid(), layoutId(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning, error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style(), null, activated(), ValueType.TEXT, url(), status());
    }

    @NonNull
    @Override
    public FieldViewModel withValue(String value) {
        return new AutoValue_BiometricsVerificationViewModel(uid(), layoutId(), label(), mandatory(),value,  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style(), null, activated(), ValueType.TEXT, url(), status());
    }

    @NonNull
    @Override
    public FieldViewModel withEditMode(boolean isEditable) {
        return new AutoValue_BiometricsVerificationViewModel(uid(), layoutId(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), isEditable, optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style(), null, activated(), ValueType.TEXT, url(), status());
    }

    @NonNull
    @Override
    public FieldViewModel withFocus(boolean isFocused) {
        return new AutoValue_BiometricsVerificationViewModel(uid(), layoutId(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style(), null, isFocused, ValueType.TEXT, url(), status());
    }

    @Nonnull
    public FieldViewModel withValueAndStatus(String data,  BiometricsVerificationView.BiometricsVerificationStatus status){
        return new AutoValue_BiometricsVerificationViewModel(uid(), layoutId(), label(), mandatory(),data, programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS_VERIFICATION, style(), null, activated(), ValueType.TEXT, url(), status);
    }

    // We don't use the FieldUiModel onItemClick() to avoid infrastructure to listen in FormViewModel
    // because we need listen in EventCaptureFormPresenter to retry verification
    public void onRetryVerificationClick() {
        if (listener != null){
            listener.onRetryClick();
        }
    }

    private BiometricsReTryOnClickListener listener;

    @NonNull
    public void setBiometricsRetryListener(BiometricsReTryOnClickListener listener) {
        this.listener = listener;
    }

    public interface BiometricsReTryOnClickListener {
        void onRetryClick();
    }

}
*/
