package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import lombok.Setter;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.FilterLabelPosition;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.function.Supplier;

public class DateFilter extends DatePicker
        implements FilterComponent<DatePicker, LocalDate> {

    private final Supplier<Condition> conditionSupplier;

    @Getter
    @Setter
    private ValueChangeMode valueChangeMode;

    public <R extends Record> DateFilter(TableField<R, Temporal> field) {
        this(field, FilterLabelPosition.TOP);
    }

    public <R extends Record> DateFilter(TableField<R, Temporal> field, FilterLabelPosition filterLabelPosition) {
        super();
        setupLabel(field, filterLabelPosition);

        setClearButtonVisible(true);

        this.conditionSupplier = () -> buildCondition(field);
        this.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private <R extends Record> Condition buildCondition(TableField<R, Temporal> field) {
        if (getValue() == null) {
            return null;
        }

        return DSL.cast(field, LocalDate.class).eq(getValue());
    }

    @Override
    public Condition getCondition() {
        return conditionSupplier.get();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void setValue(LocalDate value) {
        if (value != null) {
            super.setValue(value);
        } else {
            super.clear();
        }
    }

    @Override
    public void clear() {
        super.clear();
    }

}
