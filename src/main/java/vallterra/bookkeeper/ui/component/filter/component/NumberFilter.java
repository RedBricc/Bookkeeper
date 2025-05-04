package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import vallterra.bookkeeper.backend.util.BookkeeperCaseUtils;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;

import java.util.function.Supplier;

public class NumberFilter extends NumberField
        implements FilterComponent<NumberField, Double> {

    private Supplier<Condition> condition;

    public <R extends Record, V extends Number> NumberFilter(TableField<R, V> field) {
        super();
        this.setLabel(BookkeeperCaseUtils.snakeCaseToTitleCase(field.getName()));
        this.condition = () -> this.getValue() == null ? null : field.eq((V) this.getValue());
        this.setValueChangeMode(ValueChangeMode.EAGER);
    }

    @Override
    public Condition getCondition() {
        return condition.get();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        condition = () -> null;
    }

}
