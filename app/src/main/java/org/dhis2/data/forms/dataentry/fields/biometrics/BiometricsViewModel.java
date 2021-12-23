package org.dhis2.data.forms.dataentry.fields.biometrics;

import com.google.auto.value.AutoValue;

import org.dhis2.R;
import org.dhis2.data.forms.dataentry.DataEntryViewHolderTypes;
import org.dhis2.data.forms.dataentry.fields.FieldViewModel;
import org.dhis2.form.model.RowAction;
import org.dhis2.form.ui.event.RecyclerViewUiEvents;
import org.dhis2.form.ui.intent.FormIntent;
import org.dhis2.form.ui.style.FormUiModelStyle;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ValueType;

import androidx.annotation.NonNull;
import io.reactivex.processors.FlowableProcessor;

@AutoValue
public abstract class BiometricsViewModel extends FieldViewModel {

    public static FieldViewModel create(String id, int layoutId, String label, Boolean mandatory,
            String value, String section, Boolean editable, String description,
            ObjectStyle objectStyle, FormUiModelStyle style, String url) {
        return new AutoValue_BiometricsViewModel(id, layoutId, label, mandatory, value, section,
                null, editable, null, null, null, description, objectStyle, null,
                DataEntryViewHolderTypes.BIOMETRICS, style, null, false, ValueType.BIOMETRICS, url);
    }

    @Override
    public FieldViewModel setMandatory() {
        return new AutoValue_BiometricsViewModel(uid(), layoutId(), label(), true, value(),
                programStageSection(), allowFutureDate(), editable(), optionSet(), warning(),
                error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS,
                style(), null, activated(), ValueType.BIOMETRICS, url());
    }

    @NonNull
    @Override
    public FieldViewModel withError(@NonNull String error) {
        return new AutoValue_BiometricsViewModel(uid(), layoutId(), label(), mandatory(), value(),
                programStageSection(), allowFutureDate(), editable(), optionSet(), warning(), error,
                description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, style(),
                null, activated(), ValueType.BIOMETRICS, url());
    }

    @NonNull
    @Override
    public FieldViewModel withWarning(@NonNull String warning) {
        return new AutoValue_BiometricsViewModel(uid(), layoutId(), label(), mandatory(), value(),
                programStageSection(), allowFutureDate(), editable(), optionSet(), warning, error(),
                description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS, style(),
                null, activated(), ValueType.BIOMETRICS, url());
    }

    @NonNull
    @Override
    public FieldViewModel withValue(String value) {
        return new AutoValue_BiometricsViewModel(uid(), layoutId(), label(), mandatory(), value,
                programStageSection(), allowFutureDate(), editable(), optionSet(), warning(),
                error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS,
                style(), null, activated(), ValueType.BIOMETRICS, url());
    }

    @NonNull
    @Override
    public FieldViewModel withEditMode(boolean isEditable) {
        return new AutoValue_BiometricsViewModel(uid(), layoutId(), label(), mandatory(), value(),
                programStageSection(), allowFutureDate(), isEditable, optionSet(), warning(),
                error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS,
                style(), null, activated(), ValueType.BIOMETRICS, url());
    }

    @NonNull
    @Override
    public FieldViewModel withFocus(boolean isFocused) {
        return new AutoValue_BiometricsViewModel(uid(), layoutId(), label(), mandatory(), value(),
                programStageSection(), allowFutureDate(), editable(), optionSet(), warning(),
                error(), description(), objectStyle(), null, DataEntryViewHolderTypes.BIOMETRICS,
                style(), null, activated(), ValueType.BIOMETRICS, url());
    }
}
