package vallterra.bookkeeper.ui.component.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import vallterra.bookkeeper.backend.provider.JooqDataProvider;
import vallterra.bookkeeper.backend.util.BookkeeperCaseUtils;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.component.NumberFilter;
import vallterra.bookkeeper.ui.component.filter.component.TextFilter;
import vallterra.bookkeeper.ui.component.filter.component.ToggleableNumberRangeFilter;
import vallterra.bookkeeper.ui.component.filter.event.FilterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class BookkeeperGrid<R extends Record> extends Grid<R> {

    private final boolean includeHeaderFilters;
    private final boolean sortable;

    private final JooqDataProvider<R, Record> dataProvider;
    private final List<Consumer<FilterEvent>> filterSetListeners;
    private final List<Runnable> allFiltersClearListeners;
    private final List<Runnable> clearConditionsListeners;
    private final HeaderRow filterRow;

    public BookkeeperGrid(Table<R> table) {
        this(table, true, true);
    }

    public BookkeeperGrid(Table<R> table, boolean includeHeaderFilters) {
        this(table, includeHeaderFilters, true);
    }

    public BookkeeperGrid(Table<R> table, boolean includeHeaderFilters, boolean sortable) {
        super();

        this.includeHeaderFilters = includeHeaderFilters;
        this.sortable = sortable;

        this.dataProvider = new JooqDataProvider<>(table);
        this.filterSetListeners = new ArrayList<>();
        this.allFiltersClearListeners = new ArrayList<>();
        this.clearConditionsListeners = new ArrayList<>();

        getDataCommunicator().enablePushUpdates(Executors.newVirtualThreadPerTaskExecutor());

        setSizeFull();
        setDataProvider(dataProvider);

        filterRow = addFirstHeaderRow();

        addClassName("bookkeeper-grid");
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COMPACT);

        setColumnReorderingAllowed(true);
    }

    /**
     * Refreshes the data in the grid.
     */
    public void refreshAll() {
        dataProvider.refreshAll();
        Notification.show("Data refreshed", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /**
     * Registers a condition to the data provider without refreshing the data.
     * To apply the condition, call {@link #refreshAll()} or use {@link #applyCondition(UUID, Condition)}.
     *
     * @param condition the condition to register
     * @return the UUID of the registered condition
     */
    public UUID registerCondition(Condition condition) {
        return dataProvider.registerCondition(condition);
    }

    /**
     * Applies the condition to the data provider and refreshes the data.
     * If the condition is null, it removes the condition from the data provider.
     *
     * @param id        the UUID of the condition
     * @param condition the condition to apply
     * @return true if the condition was applied, false if it was removed
     */
    public boolean applyCondition(UUID id, @Nullable Condition condition) {
        if (condition == null) {
            removeCondition(id);
            return false;
        }

        dataProvider.applyCondition(id, condition);

        filterSetListeners.forEach(listener ->
                listener.accept(new FilterEvent(id, condition)));

        return true;
    }

    /**
     * Removes the condition from the data provider and refreshes the data.
     *
     * @param id the UUID of the condition
     */
    public void removeCondition(UUID id) {
        dataProvider.removeCondition(id);

        if (!dataProvider.hasConditions()) {
            allFiltersClearListeners.forEach(Runnable::run);
        }
    }

    /**
     * Clears all conditions from the data provider and refreshes the data.
     */
    public void clearConditions() {
        dataProvider.clearConditions();

        clearConditionsListeners.forEach(Runnable::run);
        allFiltersClearListeners.forEach(Runnable::run);
    }

    public <C extends Component, V> void registerFilter(FilterComponent<C, V> filter) {
        UUID id = registerCondition(filter.getCondition());

        filter.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                applyCondition(id, filter.getCondition());
            } else {
                removeCondition(id);
            }
        });

        addOnClearConditionsListener(filter::clear);

    }

    /**
     * Adds a new fixed condition to the data provider.
     * This condition will not be cleared by {@link #clearConditions()}.
     *
     * @param condition the condition to add
     */
    public void addFixedCondition(Condition condition) {
        dataProvider.addFixedCondition(condition);
    }

    /**
     * Adds a fixed size column to the grid with the values provided by the given TableField.
     * Defaults to a width of 90px and no flex grow.
     */
    public Column<R> addFixedSizeColumn(TableField<R, ?> tableField) {
        return addColumn(tableField)
                .setFlexGrow(0)
                .setAutoWidth(false)
                .setWidth("150px");
    }

    /**
     * Adds a route column with a link to the given target view.
     */
    public <I, V extends Component & HasUrlParameter<I>> Column<R> addRouteColumn(TableField<R, ?> labelField, TableField<R, I> idField, Class<V> target) {
        return addColumn(labelField)
                .setRenderer(new ComponentRenderer<>(v ->
                        new RouterLink(String.valueOf(v.get(labelField)), target, v.get(idField))));
    }

    /**
     * Adds a column to the grid with the values provided by the given TableField.
     */
    @SuppressWarnings("unchecked")
    public <V> Column<R> addColumn(TableField<R, V> tableField) {
        var header = BookkeeperCaseUtils.snakeCaseToTitleCase(tableField.getName());
        var column = addColumn(tableField::getValue, header)
                .setSortProperty(tableField.getName());

        if (!includeHeaderFilters) {
            return column;
        }

        return switch (tableField.getType()) {
            case Class<V> c when Number.class.isAssignableFrom(c) -> {
                FilterComponent<?, ?> numberFilter;
                if (StringUtils.equalsIgnoreCase(tableField.getName(), "id")) {
                    numberFilter = new NumberFilter((TableField<R, ? extends Number>) tableField);
                } else {
                    numberFilter = new ToggleableNumberRangeFilter((TableField<R, ? extends Number>) tableField);
                }

                addHeaderFilter(numberFilter, column);
                column.setPartNameGenerator(_ -> "end-aligned clipped");

                yield column.setTextAlign(ColumnTextAlign.END);
            }
            case Class<V> c when c == String.class -> {
                var textFilter = new TextFilter((TableField<R, String>) tableField);
                addHeaderFilter(textFilter, column);

                yield column;
            }
            default -> column;
        };
    }

    private <C extends Component, V> void addHeaderFilter(FilterComponent<C, V> numberFilter, Column<R> column) {
        numberFilter.addClassName("grid-header-filter");

        registerFilter(numberFilter);

        filterRow.getCell(column).setComponent(numberFilter.getComponent());
    }

    /**
     * Adds a column to the grid with the values provided by the given ValueProvider.
     */
    public Column<R> addColumn(ValueProvider<R, ?> valueProvider, String header) {
        return addColumn(valueProvider)
                .setHeader(header);
    }

    /**
     * Adds a column to the grid with the values provided by the given ValueProvider.
     */
    public Column<R> addColumn(ValueProvider<R, ?> valueProvider) {
        return super.addColumn(valueProvider)
                .setAutoWidth(true)
                .setSortable(sortable)
                .setResizable(true);
    }

    public Column<R> addDetailsToggleColumn() {
        return addToggleableIconButtonColumn(VaadinIcon.ANGLE_RIGHT, VaadinIcon.ANGLE_DOWN,
                item -> () -> setDetailsVisible(item, !isDetailsVisible(item)),
                this::isDetailsVisible)
                .setFlexGrow(0)
                .setAutoWidth(false)
                .setWidth("68px")
                .setFrozen(true);
    }

    public Column<R> addToggleableIconButtonColumn(IconFactory restIconFactory, IconFactory activeIconFactory, Function<R, Runnable> clickListener, Function<R, Boolean> activeCondition) {
        return addButtonColumn(new ComponentRenderer<>(item -> {
            var button = new Button();

            button.setIcon(activeCondition.apply(item) ? activeIconFactory.create() : restIconFactory.create());
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(_ -> {
                clickListener.apply(item).run();
            });

            return button;
        }));
    }

    public Column<R> addIconButtonColumn(Icon icon, Function<R, Runnable> clickListener) {
        return addButtonColumn(new ComponentRenderer<>(item -> {
            var button = new Button(icon);
            button.addClickListener(_ -> clickListener.apply(item).run());
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            return button;
        }));
    }

    public Column<R> addButtonColumn(ComponentRenderer<Button, R> buttonRenderer) {
        return super.addColumn(buttonRenderer)
                .setPartNameGenerator(_ -> "clipped")
                .setAutoWidth(true);
    }

    /**
     * Adds a listener that will be notified when a filter is set.
     */
    public void addOnFilterSetListener(Consumer<FilterEvent> listener) {
        filterSetListeners.add(listener);
    }

    /**
     * Adds a listener that will be notified when all filters are clear
     */
    public void addOnAllFiltersClearListener(Runnable listener) {
        allFiltersClearListeners.add(listener);
    }

    /**
     * Adds a listener that will be notified when all conditions are cleared
     */
    public void addOnClearConditionsListener(Runnable listener) {
        clearConditionsListeners.add(listener);
    }

}
