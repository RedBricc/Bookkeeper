package vallterra.bookkeeper.backend.provider;

import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.provider.Query;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.validation.annotation.Validated;
import vallterra.bookkeeper.ui.data.Access;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * <p>A lazy Vaadin {@link DataProvider} that fetches data from a jOOQ {@link Table}.
 * It streams only the visible page of rows to the Grid.</p>
 *
 * @param <R> the jOOQ {@link Record} type returned to the Grid
 * @param <F> filter value type (use {@link Void} if you do not support filtering)
 */
@Slf4j
@Validated
public class JooqDataProvider<R extends Record, F>
        extends AbstractBackEndDataProvider<R, F> {

    private final DSLContext db;
    @Getter
    private final Table<R> table;
    private final Map<UUID, Condition> conditionMap;
    private Condition fixedConditions;

    public JooqDataProvider(Table<R> table) {
        this.db = Access.db();
        this.table = table;
        conditionMap = new HashMap<>();
        fixedConditions = null;
    }

    @Override
    protected Stream<R> fetchFromBackEnd(Query<R, F> query) {
        if (query.getFilter().isPresent()) {
            throw new IllegalArgumentException("Filtering is not supported. Use addCondition() instead.");
        }

        var orderBy = query.getSortOrders().stream()
                .map(this::toSortField)
                .toList();

        return db.selectFrom(table)
                .where(conditionMap.values())
                .and(fixedConditions)
                .orderBy(orderBy)
                .offset(query.getOffset())
                .limit(query.getLimit())
                .fetchStream();
    }

    @Override
    protected int sizeInBackEnd(Query<R, F> query) {
        return db.selectCount()
                .from(table)
                .where(conditionMap.values())
                .and(fixedConditions)
                .fetchSingleInto(Integer.class);
    }

    private SortField<?> toSortField(QuerySortOrder order) {
        Field<?> field = table.field(order.getSorted());
        if (field == null) {
            throw new IllegalArgumentException("Field " + order.getSorted() + " not found");
        }
        return order.getDirection() == SortDirection.DESCENDING ? field.desc() : field.asc();
    }

    /**
     * Adds a condition to the data provider without refreshing the data.
     * To apply the condition, call {@link #refreshAll()} or use {@link #applyCondition(UUID, Condition)}.
     * If the condition is {@code  null}, a new ID will be generated without adding anything to the data provider.
     *
     * @param condition the condition to add
     * @return a unique identifier for the condition that can be used to update or remove it later
     */
    public UUID registerCondition(@Nullable Condition condition) {
        UUID id = UUID.randomUUID();

        if (condition != null) {
            conditionMap.put(id, condition);
        }

        return id;
    }

    /**
     * Updates the condition with the given ID and refreshes the data.
     *
     * @param id        the unique identifier of the condition to update
     * @param condition the new condition to apply
     */
    public void applyCondition(UUID id, Condition condition) {
        conditionMap.put(id, condition);
        refreshAll();
    }

    /**
     * Removes the condition with the given ID and refreshes the data.
     *
     * @param id the unique identifier of the condition to remove
     */
    public void removeCondition(UUID id) {
        conditionMap.remove(id);
        refreshAll();
    }

    /**
     * Clears all conditions and refreshes the data.
     */
    public void clearConditions() {
        conditionMap.clear();
        refreshAll();
    }

    /**
     * Checks if there are any conditions applied to the data provider.
     *
     * @return true if there are conditions, false otherwise
     */
    public boolean hasConditions() {
        return !conditionMap.isEmpty();
    }

    /**
     * Adds fixed condition to the data provider.
     * This condition is always applied to the data provider and will not be cleared by {@link #clearConditions()}.
     *
     * @param condition the fixed condition to set
     */
    public void addFixedCondition(Condition condition) {
        this.fixedConditions = fixedConditions == null ? condition : fixedConditions.and(condition);
    }

}
