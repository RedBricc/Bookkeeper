package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.generated.tables.records.CharacterRecord;
import vallterra.bookkeeper.ui.MainLayout;
import vallterra.bookkeeper.ui.component.common.BookkeeperGridLayout;

import static org.jooq.generated.tables.Character.CHARACTER;

@Route(value = "characters", layout = MainLayout.class)
@PermitAll
public class CharactersView extends BookkeeperGridLayout<CharacterRecord> {

    public CharactersView() {
        setSizeFull();

        configure(CHARACTER, "Characters");
        configureGrid();
        configureToolbar();
    }

    private void configureGrid() {
        grid().addFixedSizeColumn(CHARACTER.ID);
        grid().addColumn(CHARACTER.NAME);
        grid().addFixedSizeColumn(CHARACTER.LEVEL);
        grid().addColumn(CHARACTER.MAIN_CLASS);
        grid().addColumn(CHARACTER.RACE);
        grid().addColumn(CHARACTER.BACKGROUND);
        grid().addColumn(CHARACTER.ALIGNMENT);

        grid().asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                // Navigate to character detail view
                getUI().ifPresent(ui -> ui.navigate(CharacterDetailView.class, event.getValue().getId()));
            }
        });
    }

    private void configureToolbar() {
        var addCharacterButton = new Button("Add Character", VaadinIcon.PLUS.create());
        addCharacterButton.addClickListener(click -> {
            grid().asSingleSelect().clear();
            // Navigate to character detail view when it's created
            // getUI().ifPresent(ui -> ui.navigate("character/new"));
        });
        addActionComponent(addCharacterButton);
    }
}
