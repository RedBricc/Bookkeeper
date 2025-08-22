package vallterra.bookkeeper.ui.view.character;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.tables.pojos.Character;
import org.jooq.generated.tables.records.VCharacterAdventureRecord;
import vallterra.bookkeeper.backend.adventure.CharacterAdventureRepository;
import vallterra.bookkeeper.backend.user.CharacterRepository;
import vallterra.bookkeeper.backend.user.VallterraUserRepository;
import vallterra.bookkeeper.backend.user.WikiUserRepository;
import vallterra.bookkeeper.ui.MainLayout;
import vallterra.bookkeeper.ui.component.ExternalLink;
import vallterra.bookkeeper.ui.component.LabeledFormItem;
import vallterra.bookkeeper.ui.component.ReadOnlyTextarea;
import vallterra.bookkeeper.ui.component.grid.BookkeeperGridLayout;
import vallterra.bookkeeper.ui.data.ContextAccess;
import vallterra.bookkeeper.ui.view.adventure.AdventureDetailView;

import static org.jooq.generated.tables.VCharacterAdventure.V_CHARACTER_ADVENTURE;

@PermitAll
@UIScope
@SpringComponent
@RequiredArgsConstructor
@Route(value = "character", layout = MainLayout.class)
public class CharacterDetailView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final ContextAccess contextAccess;
    private final CharacterRepository characterRepository;
    private final WikiUserRepository wikiUserRepository;
    private final VallterraUserRepository vallterraUserRepository;
    private final CharacterAdventureRepository characterAdventureRepository;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer characterId) {
        removeAll();
        build(characterId);
    }

    private void build(final Integer characterId) {
        var maybeCharacter = characterRepository.findById(characterId);

        if (maybeCharacter.isEmpty()) {
            add(new H2("Character not found"));
            return;
        }

        var character = maybeCharacter.get();

        add(new H2(character.getName()));

        var tabs = new TabSheet();
        tabs.setSizeFull();
        add(tabs);

        tabs.add("Character", buildCharacterTab(character));
        tabs.add("Wiki User", buildUserTab(character.getWikiUserId()));
        tabs.add("Adventures", buildAdventuresTab(character));
    }

    private FormLayout buildCharacterTab(Character character) {
        var detailsLayout = new FormLayout();
        detailsLayout.setHeightFull();
        detailsLayout.getStyle().setMaxWidth("700px");

        detailsLayout.add(new LabeledFormItem("Wiki page", new ExternalLink(character.getSlug(), "https://vallterra.wiki/profile?character=")), 2);
        detailsLayout.add(new LabeledFormItem("Name", character.getName()), 1);
        detailsLayout.add(new LabeledFormItem("Short Name", character.getShortName()), 1);

        detailsLayout.add(new LabeledFormItem("Race", character.getRace()), 1);
        detailsLayout.add(new LabeledFormItem("Main Class", character.getMainClass()), 1);
        detailsLayout.add(new LabeledFormItem("Level", character.getLevel()), 1);
        detailsLayout.add(new LabeledFormItem("XP", character.getXp()), 1);

        detailsLayout.add(new LabeledFormItem("Armor Class", character.getArmorClass()), 1);
        detailsLayout.add(new LabeledFormItem("Speed", character.getSpeed()), 1);
        detailsLayout.add(new LabeledFormItem("Initiative", character.getInitiative()), 1);
        detailsLayout.add(new LabeledFormItem("Points", character.getPoints()), 1);

        detailsLayout.add(new LabeledFormItem("Alignment", character.getAlignment()), 1);
        detailsLayout.add(new LabeledFormItem("Background", character.getBackground()), 1);
        detailsLayout.add(new LabeledFormItem("Passive Perception", character.getPassivePerception()), 1);
        detailsLayout.add(new LabeledFormItem("Passive Insight", character.getPassiveInsight()), 1);

        detailsLayout.add(new LabeledFormItem("Languages", character.getLanguages()), 2);
        detailsLayout.add(new LabeledFormItem("Tools", character.getTools()), 2);

        detailsLayout.add(new ReadOnlyTextarea(character.getBio(), "Bio"), 4);
        detailsLayout.add(new ReadOnlyTextarea(character.getNotes(), "Notes"), 4);

        return detailsLayout;
    }

    private FormLayout buildUserTab(Integer wikiUserId) {
        var wikiUser = wikiUserRepository.getById(wikiUserId);
        var vallterraUser = vallterraUserRepository.getById(wikiUser.getVallterraUserId());

        var userLayout = new FormLayout();
        userLayout.setHeightFull();
        userLayout.getStyle().setMaxWidth("700px");

        userLayout.add(new LabeledFormItem("Wiki User", wikiUser.getUsername()), 1);
        userLayout.add(new LabeledFormItem("Player name", vallterraUser.getPlayerName()), 1);
        userLayout.add(new LabeledFormItem("Allow large mode", vallterraUser.getAllowLarge()), 1);
        userLayout.add(new LabeledFormItem("Prefers large mode", vallterraUser.getPrefersLarge()), 1);
        userLayout.add(new LabeledFormItem("Prefers dark mode", vallterraUser.getPrefersDark()), 1);

        return userLayout;
    }

    private BookkeeperGridLayout<VCharacterAdventureRecord> buildAdventuresTab(Character character) {
        var gridLayout = new BookkeeperGridLayout<>(V_CHARACTER_ADVENTURE, contextAccess, "Character Adventures");
        var grid = gridLayout.grid();

        grid.addFixedSizeColumn(V_CHARACTER_ADVENTURE.ID);
        grid.addColumn(V_CHARACTER_ADVENTURE.IN_PARTY);
        grid.addRouteColumn(V_CHARACTER_ADVENTURE.SLUG, V_CHARACTER_ADVENTURE.ADVENTURE_ID, AdventureDetailView.class);
        grid.addTextAreaColumn(V_CHARACTER_ADVENTURE.NOTES);
        grid.addFixedCondition(V_CHARACTER_ADVENTURE.CHARACTER_ID.eq(character.getId()));

        return gridLayout;
    }

}
