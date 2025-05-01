package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.generated.tables.pojos.Quest;
import org.springframework.beans.factory.annotation.Autowired;
import vallterra.bookkeeper.backend.quest.QuestRepository;
import vallterra.bookkeeper.ui.MainLayout;

@Route(value = "quests", layout = MainLayout.class)
@PermitAll
public class QuestsView extends VerticalLayout {

    private final QuestRepository questRepository;

    private final Grid<Quest> grid = new Grid<>();
    private final TextField nameFilter = new TextField();
    private final TextField locationFilter = new TextField();

    @Autowired
    public QuestsView(QuestRepository questRepository) {
        this.questRepository = questRepository;

        setSizeFull();
        configureGrid();

        var header = new H2("Quests");
        var toolbar = getToolbar();

        add(header, toolbar, grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(Quest::getId).setHeader("ID").setWidth("90px").setFlexGrow(0);
        grid.addColumn(Quest::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Quest::getLocation).setHeader("Location").setAutoWidth(true);
        grid.addColumn(Quest::getDifficulty).setHeader("Difficulty").setWidth("90px").setFlexGrow(0);
        grid.addColumn(Quest::getPoints).setHeader("Points").setWidth("90px").setFlexGrow(0);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                // Navigate to quest detail view
                getUI().ifPresent(ui -> ui.navigate(QuestDetailView.class, event.getValue().getId()));
            }
        });
    }

    private HorizontalLayout getToolbar() {
        nameFilter.setPlaceholder("Filter by name...");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(e -> updateList());

        locationFilter.setPlaceholder("Filter by location...");
        locationFilter.setClearButtonVisible(true);
        locationFilter.setValueChangeMode(ValueChangeMode.LAZY);
        locationFilter.addValueChangeListener(e -> updateList());

        var addQuestButton = new Button("Add Quest", VaadinIcon.PLUS.create());
        addQuestButton.addClickListener(click -> {
            grid.asSingleSelect().clear();
            // Navigate to quest detail view for creating a new quest
            getUI().ifPresent(ui -> ui.navigate(QuestDetailView.class));
        });

        var toolbar = new HorizontalLayout(nameFilter, locationFilter, addQuestButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        return toolbar;
    }

    private void updateList() {
        var nameFilterValue = nameFilter.getValue();
        var locationFilterValue = locationFilter.getValue();

        if (nameFilterValue.isEmpty() && locationFilterValue.isEmpty()) {
            grid.setItems(questRepository.findAll());
        } else if (!nameFilterValue.isEmpty() && locationFilterValue.isEmpty()) {
            grid.setItems(questRepository.findByNameContaining(nameFilterValue));
        } else if (nameFilterValue.isEmpty() && !locationFilterValue.isEmpty()) {
            grid.setItems(questRepository.findByLocationContaining(locationFilterValue));
        } else {
            // Both filters are active, need to filter in memory
            var nameFiltered = questRepository.findByNameContaining(nameFilterValue);
            grid.setItems(nameFiltered.stream()
                    .filter(quest -> quest.getLocation() != null && 
                            quest.getLocation().toLowerCase().contains(locationFilterValue.toLowerCase()))
                    .toList());
        }
    }
}
