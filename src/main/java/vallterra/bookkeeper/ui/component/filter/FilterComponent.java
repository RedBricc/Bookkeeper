package vallterra.bookkeeper.ui.component.filter;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.shared.InputField;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.shared.Registration;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import vallterra.bookkeeper.backend.debounce.Debouncer;
import vallterra.bookkeeper.backend.util.BookkeeperCaseUtils;

import java.time.Duration;

public interface FilterComponent<TComponent extends Component, TValue>
        extends InputNotifier, HasValueChangeMode, HasPlaceholder, HasAriaLabel,
        InputField<AbstractField.ComponentValueChangeEvent<TComponent, TValue>, TValue> {

    Condition getCondition();

    Component getComponent();

    void clear();

    default <R extends Record, V> void setupLabel(TableField<R, V> field, FilterLabelPosition filterLabelPosition) {
        var label = BookkeeperCaseUtils.snakeCaseToTitleCase(field.getName());
        this.setAriaLabel(field.getName());
        switch (filterLabelPosition) {
            case NONE -> this.setLabel(null);
            case TOP -> this.setLabel(label);
            case PLACEHOLDER -> this.setPlaceholder(label);
            case BOTTOM -> this.setHelperText(label);
            case RIGHT -> {
                var labelComponent = new NativeLabel(label);
                labelComponent.setFor(getComponent());

                getComponent().getElement().appendChild(labelComponent.getElement());
            }
        }
    }

    /**
     * Adds a value-change listener that is debounced on the *server side*.
     *
     * @param delay amount of idle time before the trailing call is fired
     * @return the usual Vaadin Registration so callers can remove the listener
     */
    default Registration addDebouncedValueChangeListener(
            ComponentEventListener<AbstractField.ComponentValueChangeEvent<TComponent, TValue>> listener,
            Duration delay) {

        Debouncer debouncer = new Debouncer(delay);

        Registration reg = addValueChangeListener(event ->
                debouncer.call(() ->
                        event.getSource().getUI().ifPresent(ui ->
                                ui.access(() -> listener.onComponentEvent(event)))
                )
        );

        getComponent().addDetachListener(_ -> debouncer.cancel());

        return reg;
    }

}
