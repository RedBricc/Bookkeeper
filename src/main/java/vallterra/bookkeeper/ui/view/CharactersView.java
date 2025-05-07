package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.generated.tables.records.VCharacterRecord;
import vallterra.bookkeeper.ui.MainLayout;
import vallterra.bookkeeper.ui.component.grid.BookkeeperGridLayout;
import vallterra.bookkeeper.ui.renderer.text.FormattedTextRenderer;
import vallterra.bookkeeper.ui.renderer.text.PlusPrefixedNumberTextRenderer;

import static org.jooq.generated.tables.VCharacter.V_CHARACTER;

@Route(value = "characters", layout = MainLayout.class)
@PermitAll
public class CharactersView extends BookkeeperGridLayout<VCharacterRecord> {

    public CharactersView() {
        setSizeFull();

        configure(V_CHARACTER, "Characters");
        configureGrid();
        configureToolbar();

        grid().setDetailsVisibleOnClick(false);
        grid().setItemDetailsRenderer(new ComponentRenderer<>(this::createDetailsLayout));
    }

    private void configureGrid() {
        grid().addDetailsToggleColumn();
        grid().addFixedSizeColumn(V_CHARACTER.ID).setWidth("90px");
        grid().addRouteColumn(V_CHARACTER.NAME, V_CHARACTER.ID, CharacterDetailView.class);
        grid().addFixedSizeColumn(V_CHARACTER.LEVEL);
        grid().addColumn(V_CHARACTER.MAIN_CLASS);
        grid().addFixedSizeColumn(V_CHARACTER.ARMOR_CLASS)
                .setRenderer(new FormattedTextRenderer<>(VCharacterRecord::getArmorClass, "%d AC"));
        grid().addFixedSizeColumn(V_CHARACTER.INITIATIVE)
                .setRenderer(new PlusPrefixedNumberTextRenderer<>(VCharacterRecord::getInitiative));
        grid().addFixedSizeColumn(V_CHARACTER.SPEED)
                .setRenderer(new FormattedTextRenderer<>(VCharacterRecord::getSpeed, "%d ft"));
        grid().addFixedSizeColumn(V_CHARACTER.PASSIVE_PERCEPTION);
        grid().addFixedSizeColumn(V_CHARACTER.PASSIVE_INSIGHT);
    }

    private void configureToolbar() {
        var addCharacterButton = new Button("New Character", VaadinIcon.PLUS.create());
        addCharacterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addActionComponent(addCharacterButton);
    }

    private BookkeeperGridLayout<VCharacterRecord> createDetailsLayout(VCharacterRecord character) {
        var detailsLayout = new BookkeeperGridLayout<VCharacterRecord>();
        detailsLayout.setSizeFull();
        detailsLayout.configure(V_CHARACTER, "Character details", false, DisplayMode.DETAILS);

        detailsLayout.grid().addFixedCondition(V_CHARACTER.ID.eq(character.getId()));

        detailsLayout.grid().addColumn(V_CHARACTER.PLAYER_NAME);
        detailsLayout.grid().addColumn(V_CHARACTER.RACE);
        detailsLayout.grid().addColumn(V_CHARACTER.ALIGNMENT);
        detailsLayout.grid().addColumn(V_CHARACTER.LANGUAGES);
        detailsLayout.grid().addColumn(V_CHARACTER.TOOLS);
        detailsLayout.grid().addColumn(V_CHARACTER.BACKGROUND);
        detailsLayout.grid().addColumn(V_CHARACTER.POINTS);
        detailsLayout.grid().addColumn(V_CHARACTER.ADVENTURE_COUNT);

        return detailsLayout;
    }
}
