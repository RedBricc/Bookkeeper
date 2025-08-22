package vallterra.bookkeeper.ui.view.character;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.generated.tables.records.VCharacterRecord;
import vallterra.bookkeeper.ui.MainLayout;
import vallterra.bookkeeper.ui.component.grid.BookkeeperGridLayout;
import vallterra.bookkeeper.ui.data.ContextAccess;
import vallterra.bookkeeper.ui.renderer.text.FormattedTextRenderer;
import vallterra.bookkeeper.ui.renderer.text.PlusPrefixedNumberTextRenderer;

import static org.jooq.generated.tables.VCharacter.V_CHARACTER;

@Route(value = "characters", layout = MainLayout.class)
@PermitAll
public class CharactersView extends BookkeeperGridLayout<VCharacterRecord> {

    public CharactersView(ContextAccess contextAccess) {
        super(V_CHARACTER, contextAccess, "Characters");
        setSizeFull();

        configureGrid();
        configureToolbar();

        grid().setDetailsVisibleOnClick(false);
        grid().setItemDetailsRenderer(new ComponentRenderer<>(r -> createDetailsLayout(r, contextAccess)));
    }

    private void configureGrid() {
        grid().addDetailsToggleColumn(V_CHARACTER.ID, true);
        grid().addRouteColumn(V_CHARACTER.NAME, V_CHARACTER.ID, CharacterDetailView.class);
        grid().addFixedSizeColumn(V_CHARACTER.LEVEL);
        grid().addFixedSizeColumn(V_CHARACTER.MAIN_CLASS, true);
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
        var newCharacterButton = new Button("New Character", VaadinIcon.PLUS.create());
        newCharacterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addActionComponent(newCharacterButton);
    }

    private BookkeeperGridLayout<VCharacterRecord> createDetailsLayout(VCharacterRecord character, ContextAccess contextAccess) {
        var detailsLayout = new BookkeeperGridLayout<>(V_CHARACTER, contextAccess, "Character details", false, DisplayMode.DETAILS);
        detailsLayout.setSizeFull();

        detailsLayout.grid().addFixedCondition(V_CHARACTER.ID.eq(character.getId()));

        detailsLayout.grid().addColumn(V_CHARACTER.PLAYER_NAME);
        detailsLayout.grid().addColumn(V_CHARACTER.RACE);
        detailsLayout.grid().addColumn(V_CHARACTER.ALIGNMENT);
        detailsLayout.grid().addColumn(V_CHARACTER.LANGUAGES);
        detailsLayout.grid().addColumn(V_CHARACTER.TOOLS);
        detailsLayout.grid().addColumn(V_CHARACTER.BACKGROUND);
        detailsLayout.grid().addFixedSizeColumn(V_CHARACTER.POINTS);
        detailsLayout.grid().addFixedSizeColumn(V_CHARACTER.ADVENTURE_COUNT);

        return detailsLayout;
    }
}
