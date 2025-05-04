package vallterra.bookkeeper.ui.component.common;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
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

@Validated
public abstract class BookkeeperGridLayout<R extends Record> extends VerticalLayout {

    private VerticalBorderLayout filterLayout;
    private Button filterToggle;
    private VerticalLayout filters;
    private Button clearFilters;
    private HorizontalLayout actions;
    private BookkeeperGrid<R> grid;

    public BookkeeperGridLayout() {
        super();
        setSizeFull();
    }

    protected BookkeeperGridLayout<R> configure(@NotNull Table<R> table, @NotBlank String title) {
        var refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.setTooltipText("Refresh");
        refreshButton.setThemeName("tertiary");
        refreshButton.getStyle().setMargin("0");
        refreshButton.addClickListener(_ ->
                grid.refreshAll());

        filterToggle = new Button(VaadinIcon.FILTER.create());
        filterToggle.setTooltipText("Show additional filters");
        filterToggle.setThemeName("tertiary");
        filterToggle.getStyle().setMargin("0");
        filterToggle.setVisible(false);

        clearFilters = new Button(VaadinIcon.CLOSE.create());
        clearFilters.setTooltipText("Clear filters");
        clearFilters.setThemeName("tertiary");
        clearFilters.getStyle().setMargin("0");
        clearFilters.addClickListener(_ -> {
            grid.clearConditions();
            clearFilters.setVisible(false);
        });
        clearFilters.setVisible(false);

        var titleActions = new HorizontalLayout(refreshButton, filterToggle, clearFilters);
        titleActions.setSpacing(false);

        var titleLayout = new HorizontalLayout(new H2(title), titleActions);

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

        grid = new BookkeeperGrid<>(table);
        grid.setSizeFull();

        grid.addOnFilterSetListener(_ ->
                clearFilters.setVisible(true));
        grid.addOnAllFilterClearListener(() ->
                clearFilters.setVisible(false));

        bodyLayout.add(filterLayout, grid);
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
        filterToggle.setVisible(true);
        filterLayout.setVisible(true);

        var filterComponent = filter.getComponent();
        filterComponent.getStyle().setWidth("100%");

        filters.add(filterComponent);
        grid.registerFilter(filter);

        return filterComponent;
    }

    public Component addActionComponent(Component action) {
        actions.add(action);
        return action;
    }

}
