package vallterra.bookkeeper.ui.component.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.function.ValueProvider;
import jakarta.annotation.Nullable;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import vallterra.bookkeeper.backend.provider.JooqDataProvider;
import vallterra.bookkeeper.backend.util.BookkeeperCaseUtils;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.component.NumberFilter;
import vallterra.bookkeeper.ui.component.filter.event.FilterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BookkeeperGrid<R extends Record> extends Grid<R> {

    private final JooqDataProvider<R, Record> dataProvider;
    private final List<Consumer<FilterEvent>> filterSetListeners;
    private final List<Runnable> filterClearListeners;
    private final HeaderRow filterRow;

    public BookkeeperGrid(Table<R> table) {
        super();

        this.dataProvider = new JooqDataProvider<>(table);
        this.filterSetListeners = new ArrayList<>();
        this.filterClearListeners = new ArrayList<>();

        getDataCommunicator().enablePushUpdates(Executors.newVirtualThreadPerTaskExecutor());

        setSizeFull();
        setDataProvider(dataProvider);

        addFirstHeaderRow();
        filterRow = appendHeaderRow();
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
            filterClearListeners.forEach(Runnable::run);
        }
    }

    /**
     * Clears all conditions from the data provider and refreshes the data.
     */
    public void clearConditions() {
        dataProvider.clearConditions();

        filterClearListeners.forEach(Runnable::run);
    }

    public <C extends Component, V> Component registerFilter(FilterComponent<C, V> filter) {
        UUID id = registerCondition(filter.getCondition());

        filter.addValueChangeListener(_ ->
                applyCondition(id, filter.getCondition()));

        return filter.getComponent();
    }

    /**
     * Adds a column to the grid with the values provided by the given TableField.
     */
    public <V extends Number> Column<R> addColumn(TableField<R, ?> tableField) {
        var header = BookkeeperCaseUtils.snakeCaseToTitleCase(tableField.getName());
        var column = addColumn(tableField::getValue, header)
                .setSortProperty(tableField.getName());
        filterRow.getCell(column)
                .setComponent((tableField.getType().equals(Integer.class)) ?
                        registerFilter(new NumberFilter((TableField<R, V>) tableField)) : null);
        return column;
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
                .setSortable(true)
                .setResizable(true);
    }

    /**
     * Adds a fixed size column to the grid with the values provided by the given TableField.
     * Defaults to a width of 90px and no flex grow.
     */
    public Column<R> addFixedSizeColumn(TableField<R, ?> tableField) {
        return addColumn(tableField)
                .setFlexGrow(0)
                .setAutoWidth(false)
                .setWidth("90px");
    }

    /**
     * Adds a listener that will be notified when a filter is set.
     */
    public void addOnFilterSetListener(Consumer<FilterEvent> listener) {
        filterSetListeners.add(listener);
    }

    /**
     * Adds a listener that will be notified when all filters are cleared.
     */
    public void addOnAllFilterClearListener(Runnable listener) {
        filterClearListeners.add(listener);
    }

}
