package vallterra.bookkeeper.ui.component.grid;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.validation.annotation.Validated;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.layout.VerticalBorderLayout;

@Validated
public class BookkeeperGridLayout<R extends Record> extends VerticalLayout {

    private final VerticalBorderLayout filterLayout;
    private final Button filterToggle;
    private final VerticalLayout filters;
    private final Button clearFilters;
    private final HorizontalLayout actions;
    private final BookkeeperGrid<R> grid;

    public BookkeeperGridLayout(@NotNull Table<R> table, @NotBlank String title) {
        this(table, title, true, DisplayMode.DEFAULT);
    }

    public BookkeeperGridLayout(@NotNull Table<R> table, @NotBlank String title, boolean includeHeaderFilters, @NotNull DisplayMode displayMode) {
        setSizeFull();

        grid = new BookkeeperGrid<>(table, includeHeaderFilters, DisplayMode.DEFAULT.equals(displayMode));

        var refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.setTooltipText("Refresh");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.getStyle().setMargin("0");
        refreshButton.addClickListener(_ ->
                grid.refreshAll());

        filterToggle = new Button(VaadinIcon.FILTER.create());
        filterToggle.setTooltipText("Show additional filters");
        filterToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        filterToggle.getStyle().setMargin("0");
        filterToggle.setVisible(false);

        clearFilters = new Button(VaadinIcon.CLOSE.create());
        clearFilters.setTooltipText("Clear filters");
        clearFilters.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearFilters.getStyle().setMargin("0");
        clearFilters.addClickListener(_ -> {
            grid.clearConditions();
            clearFilters.setVisible(false);
        });
        clearFilters.setVisible(false);

        var titleActions = new HorizontalLayout(refreshButton, filterToggle, clearFilters);
        titleActions.setSpacing(false);
        titleActions.setAlignItems(Alignment.CENTER);

        var titleComponent = DisplayMode.DEFAULT.equals(displayMode) ? new H2(title) : new H3(title);

        var titleLayout = new HorizontalLayout(titleComponent, titleActions);

        actions = new HorizontalLayout();
        var headerLayout = new HorizontalLayout(titleLayout, actions);

        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(headerLayout);

        var bodyLayout = new HorizontalLayout();

        var filterTitle = new H3("Additional filters");

        filters = new VerticalLayout();
        filters.setPadding(false);
        filters.setSizeFull();

        filterLayout = new VerticalBorderLayout(filterTitle, filters);
        filterLayout.setHeightFull();
        filterLayout.setWidth("300px");
        filterLayout.setVisible(false);

        filterToggle.addClickListener(_ ->
                filterLayout.setVisible(!filterLayout.isVisible()));

        grid.setSizeFull();

        grid.addOnFilterSetListener(_ ->
                clearFilters.setVisible(true));
        grid.addOnAllFiltersClearListener(() ->
                clearFilters.setVisible(false));

        bodyLayout.add(filterLayout, grid);
        bodyLayout.setSizeFull();
        add(bodyLayout);

        if (DisplayMode.DETAILS.equals(displayMode)) {
            refreshButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            filterToggle.addThemeVariants(ButtonVariant.LUMO_SMALL);
            clearFilters.addThemeVariants(ButtonVariant.LUMO_SMALL);

            grid.setAllRowsVisible(true);
            grid.addThemeName("details");
        }
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

    public FilterComponent<?, ?> addFilter(FilterComponent<?, ?> filter) {
        filterToggle.setVisible(true);
        filterLayout.setVisible(true);

        filter.getStyle().setWidth("100%");

        filters.add((Component) filter);
        grid.registerFilter(filter);

        return filter;
    }

    public Component addActionComponent(Component action) {
        actions.add(action);
        return action;
    }

    public enum DisplayMode {
        DEFAULT,
        DETAILS,
    }

}
