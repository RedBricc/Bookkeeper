package vallterra.bookkeeper.ui.component.field;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

import java.io.Serial;

/**
 * A custom Vaadin field for editing {@link YearToSecond} duration values.
 * <p>
 * The component renders six {@link NumberField}s – years, months, days, hours, minutes and seconds –
 * wrapped in a {@link FlexLayout}. Each sub‑field is configured with sensible
 * ranges and step sizes and will propagate value‑changes upstream as a single
 * {@link YearToSecond} instance, suitable for persistence with jOOQ.
 * <p>
 */
@Slf4j
public class DurationField extends CustomField<YearToSecond>
        implements HasClearButton, HasValueChangeMode {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final YearToSecond EMPTY = YearToSecond.valueOf("0-0 0 0:0:0.0");

    @Getter
    private ValueChangeMode valueChangeMode;

    private final NumberField years;
    private final NumberField months;
    private final NumberField days;
    private final NumberField hours;
    private final NumberField minutes;
    private final NumberField seconds;

    /**
     * Creates an empty {@code DurationField}.
     */
    public DurationField() {
        this(null);
    }

    /**
     * Creates a {@code DurationField} pre‑populated with an initial value.
     *
     * @param initialValue the initial {@link YearToSecond} value
     */
    public DurationField(YearToSecond initialValue) {
        setManualValidation(true);

        years = createPartField("Y", null, null);
        months = createPartField("M", 11d, years);
        days = createPartField("D", 29d, months);
        hours = createPartField("h", 23d, days);
        minutes = createPartField("m", 59d, hours);
        seconds = createPartField("s", 59d, minutes);

        setValueChangeMode(ValueChangeMode.LAZY);

        setValue(initialValue);
        add(buildLayout());
        addClassName("duration-field");
    }

    private Component buildLayout() {
        return new HorizontalLayout(years, months, days, hours, minutes, seconds);
    }

    private NumberField createPartField(String suffix, Double max, NumberField nextField) {
        var field = new NumberField();

        field.setSuffixComponent(new NativeLabel(suffix));
        field.setValueChangeMode(ValueChangeMode.LAZY);
        field.setPlaceholder("_ _");
        field.setStep(1);
        field.setMin(0.0);
        field.addValueChangeListener(event -> {
                    if (event.getValue() != null && max != null && event.isFromClient()) {
                        if (event.getValue() == max + 1) {
                            field.setValue(0d);
                            nextField.setValue(nextField.getOptionalValue().orElse(0d) + 1);
                        } else if (event.getValue() == 0d && nextField.getOptionalValue().orElse(0d) > 0d) {
                            field.setValue(max);
                            nextField.setValue(nextField.getValue() - 1);
                        }
                    }

                    var invalid = isInvalid();
                    setInvalid(invalid);

                    if (!invalid) {
                        setModelValue(generateModelValue(), true);
                    }
                }
        );

        if (max != null) field.setMax(max + 1);

        return field;
    }

    @Override
    protected YearToSecond generateModelValue() {
        var value = new YearToSecond(
                new YearToMonth(
                        safeInt(years.getValue()),
                        safeInt(months.getValue())
                ),
                new DayToSecond(
                        safeInt(days.getValue()),
                        safeInt(hours.getValue()),
                        safeInt(minutes.getValue()),
                        safeInt(seconds.getValue())
                )
        );

        if (value.equals(EMPTY)) {
            return null;
        }

        return value;
    }

    @Override
    protected void setPresentationValue(YearToSecond value) {
        if (value == null) {
            clearPartFields();
            return;
        }

        years.setValue((double) value.getYears());
        months.setValue((double) value.getMonths());
        days.setValue((double) value.getDays());
        hours.setValue((double) value.getHours());
        minutes.setValue((double) value.getMinutes());
        seconds.setValue((double) value.getSeconds());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);

        years.setReadOnly(readOnly);
        months.setReadOnly(readOnly);
        days.setReadOnly(readOnly);
        hours.setReadOnly(readOnly);
        minutes.setReadOnly(readOnly);
        seconds.setReadOnly(readOnly);
    }

    private void clearPartFields() {
        years.clear();
        months.clear();
        days.clear();
        hours.clear();
        minutes.clear();
        seconds.clear();
    }

    private static int safeInt(Double value) {
        return value == null ? 0 : value.intValue();
    }

    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.valueChangeMode = valueChangeMode;

        years.setValueChangeMode(valueChangeMode);
        months.setValueChangeMode(valueChangeMode);
        days.setValueChangeMode(valueChangeMode);
        hours.setValueChangeMode(valueChangeMode);
        minutes.setValueChangeMode(valueChangeMode);
        seconds.setValueChangeMode(valueChangeMode);
    }

    @Override
    public boolean isInvalid() {
        return years.isInvalid()
                || months.isInvalid()
                || days.isInvalid()
                || hours.isInvalid()
                || minutes.isInvalid()
                || seconds.isInvalid();
    }

}
