package vallterra.bookkeeper.ui.component.filter;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.InputNotifier;
import com.vaadin.flow.component.shared.InputField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import org.jooq.Condition;

public interface FilterComponent<TComponent extends Component, TValue>
        extends InputNotifier, HasValueChangeMode,
        InputField<AbstractField.ComponentValueChangeEvent<TComponent, TValue>, TValue> {

    Condition getCondition();

    Component getComponent();

    void clear();

}
