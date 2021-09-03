package org.dhis2.data.forms.dataentry.fields.biometrics;

import com.google.auto.value.AutoValue;

import org.dhis2.R;
import org.dhis2.data.forms.dataentry.DataEntryViewHolderTypes;
import org.dhis2.data.forms.dataentry.fields.FieldViewModel;
import org.dhis2.form.model.RowAction;
import org.dhis2.form.ui.style.FormUiModelStyle;
import org.hisp.dhis.android.core.common.ObjectStyle;

import androidx.annotation.NonNull;
import io.reactivex.processors.FlowableProcessor;

@AutoValue
public abstract class BiometricsViewModel extends FieldViewModel {

    public static FieldViewModel create(String id, String label, Boolean mandatory, String value, String section, Boolean editable, String description, ObjectStyle objectStyle, FlowableProcessor<RowAction> processor, FormUiModelStyle style,String url) {
        return new AutoValue_BiometricsViewModel(id, label, mandatory, value, section, null, editable, null, null, null, description, objectStyle, null, DataEntryViewHolderTypes.BIOMETRICS, processor, style,false, url);
    }

    @Override
    public FieldViewModel setMandatory() {
        return new AutoValue_BiometricsViewModel(uid(), label(), true,value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, processor(), style(), activated(), url());
    }

    @NonNull
    @Override
    public FieldViewModel withError(@NonNull String error) {
        return new AutoValue_BiometricsViewModel(uid(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error, description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, processor(), style(), activated(), url());
    }

    @NonNull
    @Override
    public FieldViewModel withWarning(@NonNull String warning) {
        return new AutoValue_BiometricsViewModel(uid(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning, error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, processor(), style(), activated(), url());
    }

    @NonNull
    @Override
    public FieldViewModel withValue(String value) {
        return new AutoValue_BiometricsViewModel(uid(), label(), mandatory(),value,  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, processor(), style(), activated(), url());
    }

    @NonNull
    @Override
    public FieldViewModel withEditMode(boolean isEditable) {
        return new AutoValue_BiometricsViewModel(uid(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), isEditable, optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, processor(), style(), activated(), url());
    }

    @NonNull
    @Override
    public FieldViewModel withFocus(boolean isFocused) {
        return new AutoValue_BiometricsViewModel(uid(), label(), mandatory(),value(),  programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, processor(), style(), activated(), url());
    }

    @Override
    public int getLayoutId() {
        return R.layout.form_biometrics;
    }
}
