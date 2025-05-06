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
    private final NumberFilter firstNumberFilter;
    private final NumberFilter secondNumberFilter;
    private final Button toggleButton;

    private FilterMode filterMode;
    @Getter
    private ValueChangeMode valueChangeMode;

    public <R extends Record, V extends Number> ToggleableNumberRangeFilter(TableField<R, V> field) {
        this(field, FilterLabelPosition.TOP, FilterMode.EQUAL);
    }

    public <R extends Record, V extends Number> ToggleableNumberRangeFilter(TableField<R, V> field, FilterLabelPosition filterLabelPosition, FilterMode filterMode) {
        super();

        setupLabel(field, filterLabelPosition);
        this.setTooltipText("Enter a number to filter by (inclusive)");

        firstNumberFilter = new NumberFilter(field, FilterLabelPosition.NONE);
        firstNumberFilter.setManualValidation(true);
        firstNumberFilter.addValueChangeListener(_ -> updateValue());
        firstNumberFilter.addClassName("toggleable-number-range-input");

        secondNumberFilter = new NumberFilter(field, FilterLabelPosition.NONE);
        secondNumberFilter.setManualValidation(true);
        secondNumberFilter.setVisible(false);
        secondNumberFilter.addValueChangeListener(_ -> updateValue());
        secondNumberFilter.addClassName("toggleable-number-range-input");

        toggleButton = new Button(VaadinIcon.PLUS.create());
        toggleButton.addClickListener(_ -> toggle());

        this.conditionSupplier = () -> switch (this.filterMode) {
            case EQUAL -> firstNumberFilter.getCondition();
            case RANGE -> getRangeCondition(field);
        };

        this.setValueChangeMode(ValueChangeMode.EAGER);

        setFilterMode(filterMode);

        var layout = new HorizontalLayout(firstNumberFilter, secondNumberFilter, toggleButton);
        layout.setClassName("toggleable-number-range-filter");

        add(layout);
    }

    @SuppressWarnings("unchecked")
    private <R extends Record, V extends Number> Condition getRangeCondition(TableField<R, V> field) {
        var firstValue = (V) firstNumberFilter.getValue();
        var secondValue = (V) secondNumberFilter.getValue();

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
        secondNumberFilter.setVisible(false);
        toggleButton.setIcon(VaadinIcon.PLUS.create());

    }

    private void toggleRange() {
        secondNumberFilter.setVisible(true);
        toggleButton.setIcon(VaadinIcon.MINUS.create());

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
            firstNumberFilter.clear();
            secondNumberFilter.clear();
        } else {
            firstNumberFilter.setValue(value.getMinimum().doubleValue());
            secondNumberFilter.setValue(value.getMaximum().doubleValue());
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
        if (firstNumberFilter.getValue() == null) {
            return null;
        }

        return new NumberRange<>(firstNumberFilter.getValue(), firstNumberFilter.getValue(), Comparator.comparingDouble(Number::doubleValue));
    }

    private NumberRange<Number> generateRangeModelValue() {
        var firstValue = firstNumberFilter.getOptionalValue().orElse(Double.MIN_VALUE);
        var secondValue = secondNumberFilter.getOptionalValue().orElse(Double.MAX_VALUE);

        return new NumberRange<>(firstValue, secondValue, Comparator.comparingDouble(Number::doubleValue));
    }
    @Override
    protected void setPresentationValue(NumberRange<Number> newPresentationValue) {
        if (newPresentationValue == null) {
            firstNumberFilter.clear();
            secondNumberFilter.clear();
        } else {
            firstNumberFilter.setValue(newPresentationValue.getMinimum().doubleValue());
            secondNumberFilter.setValue(newPresentationValue.getMaximum().doubleValue());
        }
    }

    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.valueChangeMode = valueChangeMode;
        firstNumberFilter.setValueChangeMode(valueChangeMode);
        secondNumberFilter.setValueChangeMode(valueChangeMode);
    }
}
