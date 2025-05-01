package vallterra.bookkeeper.ui.component.common;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.function.ValueProvider;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import vallterra.bookkeeper.backend.provider.JooqDataProvider;

import java.util.UUID;
import java.util.concurrent.Executors;

public class BookkeeperGrid<R extends Record> extends Grid<R> {

    private final JooqDataProvider<R, Record> dataProvider;

    public BookkeeperGrid(Table<R> table) {
        super();

        this.dataProvider = new JooqDataProvider<>(table);

        getDataCommunicator().enablePushUpdates(Executors.newVirtualThreadPerTaskExecutor());

        setSizeFull();
        setDataProvider(dataProvider);
    }

    /**
     * Refreshes the data in the grid.
     */
    public void refreshAll() {
        dataProvider.refreshAll();
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
     *
     * @param id        the UUID of the condition
     * @param condition the condition to apply
     */
    public void applyCondition(UUID id, Condition condition) {
        dataProvider.applyCondition(id, condition);
    }

    /**
     * Removes the condition from the data provider and refreshes the data.
     *
     * @param id the UUID of the condition
     */
    public void removeCondition(UUID id) {
        dataProvider.removeCondition(id);
    }

    /**
     * Adds a column to the grid with the values provided by the given TableField.
     */
    public Column<R> addColumn(TableField<R, ?> tableField) {
        return addColumn(tableField::getValue, tableField.getName());
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
                .setSortable(true);
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

}
