package vallterra.bookkeeper.ui.component.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class VerticalBorderLayout extends VerticalLayout {
    public VerticalBorderLayout() {
        super();
        addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.Padding.MEDIUM
        );
    }

    public VerticalBorderLayout(Component... components) {
        this();
        add(components);
    }
}
