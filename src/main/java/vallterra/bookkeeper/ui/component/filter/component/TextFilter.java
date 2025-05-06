package vallterra.bookkeeper.ui.component.filter.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.FilterLabelPosition;

import java.util.function.Supplier;

public class TextFilter extends TextField
        implements FilterComponent<TextField, String> {

    private final Supplier<Condition> conditionSupplier;
    private final boolean caseSensitive;

    public <R extends Record> TextFilter(TableField<R, String> field) {
        this(field, FilterLabelPosition.TOP, false);
    }

    public <R extends Record> TextFilter(TableField<R, String> field, FilterLabelPosition filterLabelPosition, boolean caseSensitive) {
        super();
        setupLabel(field, filterLabelPosition);

        this.conditionSupplier = () -> buildCondition(field);
        this.caseSensitive = caseSensitive;
        this.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private <R extends Record> Condition buildCondition(TableField<R, String> field) {
        if (StringUtils.isBlank(this.getValue())) {
            return null;
        }

        var cleanValue = this.getValue().trim();

        if (caseSensitive) {
            return field.contains(cleanValue);
        } else {
            return field.containsIgnoreCase(cleanValue);
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

}
