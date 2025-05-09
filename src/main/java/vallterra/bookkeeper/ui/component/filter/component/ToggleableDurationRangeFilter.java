package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import org.apache.commons.lang3.Range;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.types.YearToSecond;
import vallterra.bookkeeper.ui.component.filter.FilterLabelPosition;
import vallterra.bookkeeper.ui.component.filter.FilterMode;
import vallterra.bookkeeper.ui.component.filter.ToggleableFilterComponent;

import java.util.function.Supplier;

public class ToggleableDurationRangeFilter extends CustomField<Range<YearToSecond>>
        implements ToggleableFilterComponent<CustomField<Range<YearToSecond>>, Range<YearToSecond>> {

    private final Supplier<Condition> conditionSupplier;
    private final DurationFilter firstFilter;
    private final DurationFilter secondFilter;
    private final Button toggleButton;

    private FilterMode filterMode;
    @Getter
    private ValueChangeMode valueChangeMode;

    private static final YearToSecond MIN = YearToSecond.valueOf("-9999-12 -31 23:59:59.999999999");
    private static final YearToSecond MAX = YearToSecond.valueOf("9999-12 -31 23:59:59.999999999");

    public <R extends Record> ToggleableDurationRangeFilter(TableField<R, YearToSecond> field) {
        this(field, FilterLabelPosition.TOP, FilterMode.RANGE);
    }

    public <R extends Record> ToggleableDurationRangeFilter(TableField<R, YearToSecond> field, FilterLabelPosition filterLabelPosition, FilterMode filterMode) {
        super();
        setupLabel(field, filterLabelPosition);

        firstFilter = new DurationFilter(field, FilterLabelPosition.NONE);
        firstFilter.setManualValidation(true);
        firstFilter.addValueChangeListener(_ -> updateValue());
        firstFilter.addClassNames("toggleable-range-input", "toggleable-duration-range-input");

        secondFilter = new DurationFilter(field, FilterLabelPosition.NONE);
        secondFilter.setManualValidation(true);
        secondFilter.setVisible(false);
        secondFilter.addValueChangeListener(_ -> updateValue());
        secondFilter.addClassNames("toggleable-range-input", "toggleable-duration-range-input");

        toggleButton = new Button();
        toggleButton.addClickListener(_ -> toggle());
        toggleButton.addThemeName("filter");

        this.conditionSupplier = () -> switch (this.filterMode) {
            case EQUAL -> firstFilter.getCondition();
            case RANGE -> getRangeCondition(field);
        };

        setValueChangeMode(ValueChangeMode.LAZY);
        setFilterMode(filterMode);

        // The filter container has flex-direction: row-reverse, so we need to reverse the order of the components
        var layout = new HorizontalLayout(toggleButton, secondFilter, firstFilter);
        layout.addClassNames("toggleable-range-filter", "toggleable-duration-range-filter");

        add(layout);
    }

    private <R extends Record> Condition getRangeCondition(TableField<R, YearToSecond> field) {
        var firstValue = firstFilter.getValue();
        var secondValue = secondFilter.getValue();

        if (firstValue == null && secondValue == null) {
            return null;
        }

        if (firstValue == null) {
            return field.le(secondValue);
        }

        if (secondValue == null) {
            return field.ge(firstValue);
        }

        if (firstValue.equals(secondValue)) {
            return field.eq(firstValue);
        } else if (firstValue.compareTo(secondValue) < 0) {
            return field.ge(firstValue);
        } else {
            return field.between(firstValue, secondValue);
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Condition getCondition() {
        return conditionSupplier.get();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
        switch (filterMode) {
            case EQUAL -> toggleEqual();
            case RANGE -> toggleRange();
        }
        setModelValue(generateModelValue(), false);
    }

    private void toggleEqual() {
        secondFilter.setVisible(false);
        toggleButton.setIcon(VaadinIcon.CIRCLE.create());
        toggleButton.setTooltipText("Switch to range mode");
    }

    private void toggleRange() {
        secondFilter.setVisible(true);
        toggleButton.setIcon(VaadinIcon.ADJUST.create());
        toggleButton.setTooltipText("Switch to exact match mode");
    }

    @Override
    public void toggle() {
        switch (filterMode) {
            case EQUAL -> setFilterMode(FilterMode.RANGE);
            case RANGE -> setFilterMode(FilterMode.EQUAL);
        }
    }

    @Override
    public void setValue(Range<YearToSecond> value) {
        if (value == null) {
            firstFilter.clear();
            secondFilter.clear();
        } else {
            firstFilter.setValue(value.getMinimum());
            secondFilter.setValue(value.getMaximum());
        }
    }

    @Override
    protected Range<YearToSecond> generateModelValue() {
        return switch (filterMode) {
            case EQUAL -> generateEqualModelValue();
            case RANGE -> generateRangeModelValue();
        };
    }

    private Range<YearToSecond> generateEqualModelValue() {
        if (firstFilter.getValue() == null) {
            return null;
        }

        return Range.of(firstFilter.getValue(), firstFilter.getValue());
    }

    private Range<YearToSecond> generateRangeModelValue() {
        var firstValue = firstFilter.getOptionalValue().orElse(MIN);
        var secondValue = secondFilter.getOptionalValue().orElse(MAX);

        return Range.of(firstValue, secondValue);
    }

    @Override
    protected void setPresentationValue(Range<YearToSecond> newPresentationValue) {
        if (newPresentationValue == null) {
            firstFilter.clear();
            secondFilter.clear();
        } else {
            firstFilter.setValue(newPresentationValue.getMinimum());
            secondFilter.setValue(newPresentationValue.getMaximum());
        }
    }

    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.valueChangeMode = valueChangeMode;

        firstFilter.setValueChangeMode(valueChangeMode);
        secondFilter.setValueChangeMode(valueChangeMode);
    }
}
