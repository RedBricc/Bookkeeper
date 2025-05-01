package vallterra.bookkeeper.ui.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.InputNotifier;
import com.vaadin.flow.component.shared.InputField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import org.jooq.Condition;

public abstract class FilterComponent<TComponent extends Component, TValue> extends Component
        implements InputNotifier, HasValueChangeMode,
        InputField<AbstractField.ComponentValueChangeEvent<TComponent, TValue>, TValue> {

    public abstract Condition getCondition();

    public abstract void clear();

}
