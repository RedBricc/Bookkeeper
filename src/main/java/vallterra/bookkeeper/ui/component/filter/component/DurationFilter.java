package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.types.YearToSecond;
import vallterra.bookkeeper.ui.component.field.DurationField;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.FilterLabelPosition;

import java.util.function.Supplier;

public class DurationFilter extends DurationField
        implements FilterComponent<CustomField<YearToSecond>, YearToSecond> {

    private final Supplier<Condition> conditionSupplier;

    public <R extends Record> DurationFilter(TableField<R, YearToSecond> field) {
        this(field, FilterLabelPosition.TOP);
    }

    public <R extends Record> DurationFilter(TableField<R, YearToSecond> field, FilterLabelPosition filterLabelPosition) {
        super();
        setupLabel(field, filterLabelPosition);

        setClearButtonVisible(true);
        setValueChangeMode(ValueChangeMode.LAZY);

        this.conditionSupplier = () -> buildCondition(field);
    }

    @Override
    public Condition getCondition() {
        return conditionSupplier.get();
    }

    private <R extends Record> Condition buildCondition(TableField<R, YearToSecond> field) {
        if (getValue() == null) {
            return null;
        }

        return field.eq(getValue());
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        super.clear();
    }

}
