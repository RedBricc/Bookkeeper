package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.FilterLabelPosition;

import java.util.function.Supplier;

public class NumberFilter extends NumberField
        implements FilterComponent<NumberField, Double> {

    private final Supplier<Condition> condition;

    public <R extends Record, V extends Number> NumberFilter(TableField<R, V> field) {
        this(field, FilterLabelPosition.TOP);
    }

    public <R extends Record, V extends Number> NumberFilter(TableField<R, V> field, FilterLabelPosition filterLabelPosition) {
        super();

        setupLabel(field, filterLabelPosition);
        this.setTooltipText("Enter a number to filter by (inclusive)");

        this.condition = () -> this.getValue() == null ? null : field.eq((V) this.getValue());
        this.setValueChangeMode(ValueChangeMode.EAGER);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Condition getCondition() {
        return condition.get();
    }

    @Override
    public void clear() {
        super.clear();
    }

}
