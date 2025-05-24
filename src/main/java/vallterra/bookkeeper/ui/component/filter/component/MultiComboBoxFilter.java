package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import lombok.Setter;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.FilterLabelPosition;
import vallterra.bookkeeper.ui.data.ContextAccess;

import java.util.Set;
import java.util.function.Supplier;

public class MultiComboBoxFilter<T> extends MultiSelectComboBox<T>
        implements FilterComponent<MultiSelectComboBox<T>, Set<T>> {

    private final Supplier<Condition> conditionSupplier;

    @Getter
    @Setter
    private ValueChangeMode valueChangeMode;

    public <R extends Record> MultiComboBoxFilter(TableField<R, T> field, ContextAccess contextAccess) {
        this(field, contextAccess, FilterLabelPosition.TOP);
    }

    public <R extends Record> MultiComboBoxFilter(TableField<R, T> field, ContextAccess contextAccess, FilterLabelPosition filterLabelPosition) {
        super();
        setupLabel(field, filterLabelPosition);

        setItems(field, contextAccess);

        this.conditionSupplier = () -> buildCondition(field);
        this.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private <R extends Record> Condition buildCondition(TableField<R, T> field) {
        if (this.getValue().isEmpty()) {
            return null;
        }

        return field.in(this.getValue());
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
    public void clear() {
        super.clear();
    }

    private <R extends Record> void setItems(TableField<R, T> field, ContextAccess contextAccess) {
        var items = contextAccess.db().selectDistinct(field)
                .from(field.getTable())
                .fetchInto(field.getType());
        this.setItems(items);
    }

}
