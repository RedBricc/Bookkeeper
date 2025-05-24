package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import org.apache.commons.lang3.NumberRange;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import vallterra.bookkeeper.ui.component.filter.FilterLabelPosition;
import vallterra.bookkeeper.ui.component.filter.FilterMode;
import vallterra.bookkeeper.ui.component.filter.ToggleableFilterComponent;

import java.util.Comparator;
import java.util.function.Supplier;

public class ToggleableNumberRangeFilter extends CustomField<NumberRange<Number>>
        implements ToggleableFilterComponent<CustomField<NumberRange<Number>>, NumberRange<Number>> {

    private final Supplier<Condition> conditionSupplier;
    private final NumberFilter firstFilter;
    private final NumberFilter secondFilter;
    private final Button toggleButton;

    private FilterMode filterMode;
    @Getter
    private ValueChangeMode valueChangeMode;

    public <R extends Record, V extends Number> ToggleableNumberRangeFilter(TableField<R, V> field) {
        this(field, FilterLabelPosition.TOP, FilterMode.RANGE);
    }

    public <R extends Record, V extends Number> ToggleableNumberRangeFilter(TableField<R, V> field, FilterLabelPosition filterLabelPosition, FilterMode filterMode) {
        super();

        setupLabel(field, filterLabelPosition);

        firstFilter = new NumberFilter(field, FilterLabelPosition.NONE);
        firstFilter.setManualValidation(true);
        firstFilter.addValueChangeListener(_ -> updateValue());
        firstFilter.addClassNames("toggleable-range-input", "toggleable-number-range-input");

        secondFilter = new NumberFilter(field, FilterLabelPosition.NONE);
        secondFilter.setManualValidation(true);
        secondFilter.setVisible(false);
        secondFilter.addValueChangeListener(_ -> updateValue());
        secondFilter.addClassNames("toggleable-range-input", "toggleable-number-range-input");

        toggleButton = new Button();
        toggleButton.addClickListener(_ -> toggle());
        toggleButton.addThemeName("filter");

        this.conditionSupplier = () -> switch (this.filterMode) {
            case EQUAL -> firstFilter.getCondition();
            case RANGE -> getRangeCondition(field);
        };

        this.setValueChangeMode(ValueChangeMode.LAZY);

        setFilterMode(filterMode);

        // The filter container has flex-direction: row-reverse, so we need to reverse the order of the components
        var layout = new HorizontalLayout(toggleButton, secondFilter, firstFilter);
        layout.addClassNames("toggleable-range-filter", "toggleable-number-range-filter");

        add(layout);
    }

    @SuppressWarnings("unchecked")
    private <R extends Record, V extends Number> Condition getRangeCondition(TableField<R, V> field) {
        var firstValue = (V) firstFilter.getValue();
        var secondValue = (V) secondFilter.getValue();

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
        } else if (firstValue.doubleValue() > secondValue.doubleValue()) {
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
    public void setValue(NumberRange<Number> value) {
        if (value == null) {
            firstFilter.clear();
            secondFilter.clear();
        } else {
            firstFilter.setValue(value.getMinimum().doubleValue());
            secondFilter.setValue(value.getMaximum().doubleValue());
        }
    }

    @Override
    protected NumberRange<Number> generateModelValue() {
        return switch (filterMode) {
            case EQUAL -> generateEqualModelValue();
            case RANGE -> generateRangeModelValue();
        };
    }

    private NumberRange<Number> generateEqualModelValue() {
        if (firstFilter.getValue() == null) {
            return null;
        }

        return new NumberRange<>(firstFilter.getValue(), firstFilter.getValue(), Comparator.comparingDouble(Number::doubleValue));
    }

    private NumberRange<Number> generateRangeModelValue() {
        var firstValue = firstFilter.getOptionalValue().orElse(Double.MIN_VALUE);
        var secondValue = secondFilter.getOptionalValue().orElse(Double.MAX_VALUE);

        return new NumberRange<>(firstValue, secondValue, Comparator.comparingDouble(Number::doubleValue));
    }
    @Override
    protected void setPresentationValue(NumberRange<Number> newPresentationValue) {
        if (newPresentationValue == null) {
            firstFilter.clear();
            secondFilter.clear();
        } else {
            firstFilter.setValue(newPresentationValue.getMinimum().doubleValue());
            secondFilter.setValue(newPresentationValue.getMaximum().doubleValue());
        }
    }

    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.valueChangeMode = valueChangeMode;

        firstFilter.setValueChangeMode(valueChangeMode);
        secondFilter.setValueChangeMode(valueChangeMode);
    }
}
