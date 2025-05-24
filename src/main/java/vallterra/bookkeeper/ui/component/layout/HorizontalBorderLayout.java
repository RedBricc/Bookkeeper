package vallterra.bookkeeper.ui.component.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class HorizontalBorderLayout extends HorizontalLayout {

    public HorizontalBorderLayout() {
        super();
        addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.Padding.MEDIUM
        );
    }

    public HorizontalBorderLayout(Component... components) {
        this();
        add(components);
    }
}
