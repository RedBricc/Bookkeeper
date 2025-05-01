package vallterra.bookkeeper.ui.component.common;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.validation.annotation.Validated;
import vallterra.bookkeeper.ui.component.FilterComponent;

import java.util.UUID;

@Validated
public abstract class BookkeeperGridLayout<R extends Record> extends VerticalLayout {

    private VerticalLayout filters;
    private HorizontalLayout actions;
    private BookkeeperGrid<R> grid;

    public BookkeeperGridLayout() {
        super();
        setSizeFull();
    }

    protected BookkeeperGridLayout<R> configure(@NotNull Table<R> table, @NotBlank String title) {
        var filterToggle = new Button(VaadinIcon.FILTER.create());
        var titleLayout = new HorizontalLayout(new H2(title), filterToggle);

        actions = new HorizontalLayout();
        var headerLayout = new HorizontalLayout(titleLayout, actions);

        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setPadding(true);

        add(headerLayout);

        var bodyLayout = new HorizontalLayout();

        filters = new VerticalLayout();
        filters.setWidth("300px");
        filters.setHeightFull();

        filterToggle.addClickListener(event -> {
            filters.setVisible(!filters.isVisible());
        });

        grid = new BookkeeperGrid<>(table);
        grid.setSizeFull();

        bodyLayout.add(filters, grid);
        bodyLayout.setSizeFull();
        add(bodyLayout);

        return this;
    }

    public BookkeeperGrid<R> grid() {
        return grid;
    }

    /**
     * Refreshes the data in the grid.
     */
    public void refreshAll() {
        grid.refreshAll();
    }

    public Component addFilter(FilterComponent<?, ?> filter) {
        filters.add(filter);
        UUID id = grid.registerCondition(filter.getCondition());

        filter.addValueChangeListener(event -> {
            grid.applyCondition(id, filter.getCondition());
        });

        return filter;
    }

    public Component addActionComponent(Component action) {
        actions.add(action);
        return action;
    }

}
