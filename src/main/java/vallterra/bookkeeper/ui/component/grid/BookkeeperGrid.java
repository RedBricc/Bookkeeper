package vallterra.bookkeeper.ui.component.grid;

import com.helger.commons.datetime.OffsetDate;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.*;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.types.YearToSecond;
import vallterra.bookkeeper.backend.provider.JooqDataProvider;
import vallterra.bookkeeper.backend.util.BookkeeperCaseUtils;
import vallterra.bookkeeper.backend.util.BookkeeperDateTimeUtils;
import vallterra.bookkeeper.ui.component.filter.FilterComponent;
import vallterra.bookkeeper.ui.component.filter.component.*;
import vallterra.bookkeeper.ui.component.filter.event.FilterEvent;
import vallterra.bookkeeper.ui.data.ContextAccess;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class BookkeeperGrid<R extends Record> extends Grid<R> {

    private final boolean includeHeaderFilters;
    private final boolean sortable;

    private final ContextAccess contextAccess;

    private final JooqDataProvider<R, Record> dataProvider;
    private final List<Consumer<FilterEvent>> filterSetListeners;
    private final List<Runnable> allFiltersClearListeners;
    private final List<Runnable> clearConditionsListeners;
    private final HeaderRow filterRow;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern());
    private final DateTimeFormatter offsetDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm ZZ");

    private final Map<Class<? extends Temporal>, Function<TableField<R, ? extends Temporal>, Renderer<R>>> temporalFormatLookupMap = Map.of(
            LocalDate.class, tableField -> new LocalDateRenderer<>(t ->
                    (LocalDate) t.getValue(tableField), DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern()),
            OffsetDate.class, tableField -> new TextRenderer<>(t ->
                    t.getValue(tableField) == null ? null : ((OffsetDate) t.getValue(tableField)).format(dateFormatter)),
            LocalDateTime.class, tableField -> new LocalDateTimeRenderer<>(t ->
                    (LocalDateTime) t.getValue(tableField), "yyyy-MM-dd HH:mm"),
            OffsetDateTime.class, tableField -> new TextRenderer<>(t ->
                    t.getValue(tableField) == null ? null : ((OffsetDateTime) t.getValue(tableField)).format(offsetDateTimeFormatter))
    );

    public BookkeeperGrid(Table<R> table, ContextAccess contextAccess) {
        this(table, contextAccess, true, true);
    }

    public BookkeeperGrid(Table<R> table, ContextAccess contextAccess, boolean includeHeaderFilters) {
        this(table, contextAccess, includeHeaderFilters, true);
    }

    public BookkeeperGrid(Table<R> table, ContextAccess contextAccess, boolean includeHeaderFilters, boolean sortable) {
        super();

        this.includeHeaderFilters = includeHeaderFilters;
        this.sortable = sortable;

        this.contextAccess = contextAccess;

        this.dataProvider = new JooqDataProvider<>(table, contextAccess);
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

        if (filter.getValueChangeMode() == ValueChangeMode.EAGER) {
            filter.addValueChangeListener(event ->
                    updateCondition(filter, event, id));
        } else {
            filter.addDebouncedValueChangeListener(event ->
                            updateCondition(filter, event, id),
                    Duration.ofMillis(400));

            addOnClearConditionsListener(filter::clear);
        }

    }

    private <C extends Component, V> void updateCondition(FilterComponent<C, V> filter, AbstractField.ComponentValueChangeEvent<C, V> event, UUID id) {
        if (event.getValue() != null) {
            applyCondition(id, filter.getCondition());
        } else {
            removeCondition(id);
        }
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
        return addFixedSizeColumn(tableField, false);
    }

    /**
     * Adds a fixed size column to the grid with the values provided by the given TableField.
     *
     * @param tableField the TableField to use as the value provider
     * @param width      the width of the column in pixels
     */
    public Column<R> addFixedSizeColumn(TableField<R, ?> tableField, Integer width) {
        return addFixedSizeColumn(tableField, width, false);
    }

    /**
     * Adds a fixed size column to the grid with the values provided by the given TableField.
     * Defaults to a width of 150px and no flex grow.
     *
     * @param tableField           the TableField to use as the value provider
     * @param useAlternativeFilter if true, use the alternative filter component. The specific filter component is dependent on the type of the TableField.
     */
    public Column<R> addFixedSizeColumn(TableField<R, ?> tableField, boolean useAlternativeFilter) {
        return addFixedSizeColumn(tableField, 150, useAlternativeFilter);
    }

    /**
     * Adds a fixed size column to the grid with the values provided by the given TableField.
     *
     * @param tableField           the TableField to use as the value provider
     * @param width                the width of the column in pixels
     * @param useAlternativeFilter if true, use the alternative filter component. The specific filter component is dependent on the type of the TableField.
     */
    public Column<R> addFixedSizeColumn(TableField<R, ?> tableField, Integer width, boolean useAlternativeFilter) {
        return addColumn(tableField, useAlternativeFilter)
                .setFlexGrow(0)
                .setAutoWidth(false)
                .setWidth(width + "px");
    }

    public Column<R> addFrozenColumn(TableField<R, ?> tableField, boolean useAlternativeFilter) {
        return addFixedSizeColumn(tableField, 100, useAlternativeFilter)
                .setFrozen(true);
    }

    /**
     * Adds a fixed route column with a link to the given target view.
     */
    public <V extends Component & ThemableLayout> Column<R> addFixedRouteColumn(TableField<R, ?> labelField, Class<V> target) {
        return addFixedSizeColumn(labelField)
                .setRenderer(new ComponentRenderer<>(v ->
                        new RouterLink(Objects.toString(v.get(labelField), ""), target)));
    }


    /**
     * Adds a fixed route column with a link to the given target view.
     */
    public <V extends Component & ThemableLayout> Column<R> addFixedRouteColumn(TableField<R, ?> labelField, Class<V> target, Integer width) {
        return addFixedSizeColumn(labelField, width)
                .setRenderer(new ComponentRenderer<>(v ->
                        new RouterLink(Objects.toString(v.get(labelField), ""), target)));
    }

    /**
     * Adds a route column with a link to the given target view.
     */
    public <V extends Component & ThemableLayout> Column<R> addRouteColumn(TableField<R, ?> labelField, Class<V> target) {
        return addColumn(labelField)
                .setRenderer(new ComponentRenderer<>(v ->
                        new RouterLink(Objects.toString(v.get(labelField), ""), target)));
    }

    /**
     * Adds a route column with a link to the given target view filtered for the given id.
     */
    public <I, V extends Component & HasUrlParameter<I>> Column<R> addRouteColumn(TableField<R, ?> labelField, TableField<R, I> idField, Class<V> target) {
        return addColumn(labelField)
                .setRenderer(new ComponentRenderer<>(v ->
                        new RouterLink(Objects.toString(v.get(labelField), ""), target, v.get(idField))));
    }

    public <V> Column<R> addTextAreaColumn(TableField<R, V> tableField) {
        return addColumn(tableField, false)
                .setRenderer(new ComponentRenderer<>(item -> {
                    var textArea = new TextArea();

                    textArea.setValue(Objects.toString(tableField.getValue(item), ""));
                    textArea.setReadOnly(true);
                    textArea.setWidthFull();
                    textArea.addClassName("bookkeeper-text-area");

                    return textArea;
                }))
                .setSortable(false);
    }

    public <V> Column<R> addVallterraLinkColumn(TableField<R, V> pathTableField) {
        return addVallterraLinkColumn(pathTableField, "/");
    }

    public <V> Column<R> addVallterraLinkColumn(TableField<R, V> slugTableField, String path) {
        return addExternalLinkColumn(slugTableField, "https://vallterra.wiki" + path);
    }

    public <V> Column<R> addExternalLinkColumn(TableField<R, V> addVallterraLinkColumn, String urlBase) {
        return addFixedSizeColumn(addVallterraLinkColumn, false)
                .setRenderer(new ComponentRenderer<>(item -> {
                    var path = Objects.toString(addVallterraLinkColumn.getValue(item), "");
                    var anchor = new Anchor(urlBase + path);

                    anchor.setText(path);
                    anchor.setTarget("_blank");

                    return anchor;
                }));
    }

    public <V> Column<R> addColumn(TableField<R, V> tableField) {
        return addColumn(tableField, false);
    }

    /**
     * Adds a column to the grid with the values provided by the given TableField.
     *
     * @param tableField           the TableField to use as the value provider
     * @param useAlternativeFilter if true, use the alternative filter component. The specific filter component is dependent on the type of the TableField.
     */
    @SuppressWarnings("unchecked")
    public <V> Column<R> addColumn(TableField<R, V> tableField, boolean useAlternativeFilter) {
        var header = BookkeeperCaseUtils.snakeCaseToTitleCase(tableField.getName());
        var column = addColumn(tableField::getValue, header)
                .setSortProperty(tableField.getName());

        switch (tableField.getType()) {
            case Class<V> c when c == String.class ->
                    applyStringHeaderFilter((TableField<R, String>) tableField, contextAccess, useAlternativeFilter, column);
            case Class<V> c when c == YearToSecond.class -> {
                applyDurationHeaderFilter((TableField<R, YearToSecond>) tableField, useAlternativeFilter, column);

                column.setRenderer(new TextRenderer<>(t ->
                                t.getValue(tableField) == null ? null : BookkeeperDateTimeUtils.formatDuration((YearToSecond) t.getValue(tableField))))
                        .setPartNameGenerator(_ -> "end-aligned clipped")
                        .setTextAlign(ColumnTextAlign.END)
                        .setFlexGrow(0)
                        .setAutoWidth(false)
                        .setWidth("330px");
            }
            case Class<V> c when Temporal.class.isAssignableFrom(c) -> {
                applyTemporalHeaderFilter((TableField<R, Temporal>) tableField, useAlternativeFilter, column);

                column.setRenderer(temporalFormatLookupMap.get(c).apply((TableField<R, ? extends Temporal>) tableField));
            }
            case Class<V> c when Number.class.isAssignableFrom(c) -> {
                applyNumberHeaderFilter((TableField<R, ? extends Number>) tableField, useAlternativeFilter, column);

                column.setPartNameGenerator(_ -> "end-aligned clipped")
                        .setTextAlign(ColumnTextAlign.END);
            }
            default -> log.warn("Unsupported column type: {}", tableField.getType());
        }

        return column;
    }

    private void applyTemporalHeaderFilter(TableField<R, Temporal> tableField, boolean useAlternativeFilter, Column<R> column) {
        if (!includeHeaderFilters) {
            return;
        }

        FilterComponent<?, ?> filterComponent;
        if (useAlternativeFilter) {
            filterComponent = new DateFilter(tableField);
        } else {
            filterComponent = new ToggleableDateRangeFilter(tableField);
        }

        addHeaderFilter(filterComponent, column);
    }

    private void applyStringHeaderFilter(TableField<R, String> tableField, ContextAccess contextAccess, boolean useAlternativeFilter, Column<R> column) {
        if (!includeHeaderFilters) {
            return;
        }

        FilterComponent<?, ?> filterComponent;
        if (useAlternativeFilter) {
            filterComponent = new MultiComboBoxFilter<>(tableField, contextAccess);
        } else {
            filterComponent = new TextFilter(tableField);
        }

        addHeaderFilter(filterComponent, column);
    }

    private void applyDurationHeaderFilter(TableField<R, YearToSecond> tableField, boolean useAlternativeFilter, Column<R> column) {
        if (!includeHeaderFilters) {
            return;
        }

        FilterComponent<?, ?> durationFilter;
        if (useAlternativeFilter) {
            durationFilter = new DurationFilter(tableField);
        } else {
            durationFilter = new ToggleableDurationRangeFilter(tableField);
        }

        addHeaderFilter(durationFilter, column);
    }

    private void applyNumberHeaderFilter(TableField<R, ? extends Number> tableField, boolean useAlternativeFilter, Column<R> column) {
        if (!includeHeaderFilters) {
            return;
        }

        FilterComponent<?, ?> numberFilter;
        if (useAlternativeFilter) {
            numberFilter = new NumberFilter(tableField);
        } else {
            numberFilter = new ToggleableNumberRangeFilter(tableField);
        }

        addHeaderFilter(numberFilter, column);
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

    public <V> Column<R> addDetailsToggleColumn(TableField<R, V> tableField) {
        return addDetailsToggleColumn(tableField, false);
    }

    public <V> Column<R> addDetailsToggleColumn(TableField<R, V> tableField, boolean useAlternativeFilter) {
        return addToggleableIconButtonColumn(tableField, useAlternativeFilter,
                VaadinIcon.ANGLE_RIGHT, VaadinIcon.ANGLE_DOWN,
                item -> () -> setDetailsVisible(item, !isDetailsVisible(item)),
                this::isDetailsVisible)
                .setFlexGrow(0)
                .setAutoWidth(false)
                .setWidth("100px")
                .setFrozen(true);
    }

    public <V> Column<R> addToggleableIconButtonColumn(TableField<R, V> tableField, boolean useAlternativeFilter, IconFactory restIconFactory, IconFactory activeIconFactory, Function<R, Runnable> clickListener, Function<R, Boolean> activeCondition) {
        return addColumn(tableField, useAlternativeFilter)
                .setRenderer(new ComponentRenderer<>(item -> {
                    var button = new Button(Objects.toString(tableField.getValue(item), ""));

                    button.setIcon(activeCondition.apply(item) ? activeIconFactory.create() : restIconFactory.create());
                    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
                    button.addThemeName("width-full body-text-label");
                    button.addClickListener(_ -> clickListener.apply(item).run());

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
