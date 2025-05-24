package vallterra.bookkeeper.ui.component.filter;

import com.vaadin.flow.component.Component;

public interface ToggleableFilterComponent<TComponent extends Component, TValue>
        extends FilterComponent<TComponent, TValue> {

    void setFilterMode(FilterMode filterMode);

    void toggle();

}
