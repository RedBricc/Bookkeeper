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
import org.jooq.generated.tables.pojos.Adventure;
import org.springframework.beans.factory.annotation.Autowired;
import vallterra.bookkeeper.backend.adventure.AdventureRepository;
import vallterra.bookkeeper.ui.MainLayout;

@Route(value = "adventures", layout = MainLayout.class)
@PermitAll
public class AdventuresView extends VerticalLayout {

    private final AdventureRepository adventureRepository;

    private final Grid<Adventure> grid = new Grid<>();
    private final TextField slugFilter = new TextField();

    @Autowired
    public AdventuresView(AdventureRepository adventureRepository) {
        this.adventureRepository = adventureRepository;

        setSizeFull();
        configureGrid();

        var header = new H2("Adventures");
        var toolbar = getToolbar();

        add(header, toolbar, grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(Adventure::getId).setHeader("ID").setWidth("90px").setFlexGrow(0);
        grid.addColumn(Adventure::getQuestId).setHeader("Quest ID").setWidth("90px").setFlexGrow(0);
        grid.addColumn(Adventure::getSlug).setHeader("Slug");
        grid.addColumn(Adventure::getCreatedAt).setHeader("Created At");
        grid.addColumn(Adventure::getUpdatedAt).setHeader("Updated At");

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                // Navigate to adventure detail view
                getUI().ifPresent(ui -> ui.navigate(AdventureDetailView.class, event.getValue().getId()));
            }
        });
    }

    private HorizontalLayout getToolbar() {
        slugFilter.setPlaceholder("Filter by slug...");
        slugFilter.setClearButtonVisible(true);
        slugFilter.setValueChangeMode(ValueChangeMode.LAZY);
        slugFilter.addValueChangeListener(e -> updateList());

        var addAdventureButton = new Button("Add Adventure", VaadinIcon.PLUS.create());
        addAdventureButton.addClickListener(click -> {
            grid.asSingleSelect().clear();
            // Navigate to adventure detail view for creating a new adventure
            getUI().ifPresent(ui -> ui.navigate(AdventureDetailView.class));
        });

        var toolbar = new HorizontalLayout(slugFilter, addAdventureButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        return toolbar;
    }

    private void updateList() {
        var slugFilterValue = slugFilter.getValue();

        if (slugFilterValue.isEmpty()) {
            grid.setItems(adventureRepository.findAll());
        } else {
            grid.setItems(adventureRepository.findBySlugContaining(slugFilterValue));
        }
    }
}
