package vallterra.bookkeeper.ui.component.filter;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.InputNotifier;
import com.vaadin.flow.component.shared.InputField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import vallterra.bookkeeper.backend.util.BookkeeperCaseUtils;

public interface FilterComponent<TComponent extends Component, TValue>
        extends InputNotifier, HasValueChangeMode, HasPlaceholder,
        InputField<AbstractField.ComponentValueChangeEvent<TComponent, TValue>, TValue> {

    Condition getCondition();

    Component getComponent();

    void clear();

    default <R extends Record, V> void setupLabel(TableField<R, V> field, FilterLabelPosition filterLabelPosition) {
        var label = BookkeeperCaseUtils.snakeCaseToTitleCase(field.getName());
        switch (filterLabelPosition) {
            case NONE -> this.setLabel(null);
            case TOP -> this.setLabel(label);
            case PLACEHOLDER -> this.setPlaceholder(label);
            case BOTTOM -> this.setHelperText(label);
        }
    }

}
