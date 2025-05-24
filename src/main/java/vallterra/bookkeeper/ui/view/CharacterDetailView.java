package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.generated.tables.pojos.Adventure;
import org.jooq.generated.tables.pojos.Character;
import org.jooq.generated.tables.pojos.CharacterAdventure;
import org.jooq.generated.tables.pojos.VallterraUser;
import org.springframework.beans.factory.annotation.Autowired;
import vallterra.bookkeeper.backend.adventure.AdventureRepository;
import vallterra.bookkeeper.backend.adventure.CharacterAdventureRepository;
import vallterra.bookkeeper.backend.user.CharacterRepository;
import vallterra.bookkeeper.backend.user.VallterraUserRepository;
import vallterra.bookkeeper.ui.MainLayout;

import java.util.Optional;

@Route(value = "character", layout = MainLayout.class)
@PermitAll
public class CharacterDetailView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final CharacterRepository characterRepository;
    private final VallterraUserRepository vallterraUserRepository;
    private final AdventureRepository adventureRepository;
    private final CharacterAdventureRepository characterAdventureRepository;

    private Character character;
    private final Grid<CharacterAdventure> adventuresGrid = new Grid<>(CharacterAdventure.class);

    // Form fields
    private final ComboBox<VallterraUser> userComboBox = new ComboBox<>("Vallterra User");
    private final TextField nameField = new TextField("Name");
    private final TextField raceField = new TextField("Race");
    private final TextField classField = new TextField("Class");
    private final IntegerField levelField = new IntegerField("Level");
    private final TextField backgroundField = new TextField("Background");
    private final TextField alignmentField = new TextField("Alignment");
    private final TextField languagesField = new TextField("Languages");
    private final IntegerField speedField = new IntegerField("Speed");
    private final TextField toolsField = new TextField("Tools");
    private final IntegerField passivePerceptionField = new IntegerField("Passive Perception");
    private final IntegerField passiveInsightField = new IntegerField("Passive Insight");
    private final IntegerField initiativeField = new IntegerField("Initiative");
    private final IntegerField armorClassField = new IntegerField("Armor Class");
    private final TextArea bioField = new TextArea("Bio");
    private final IntegerField pointsField = new IntegerField("Points");
    private final TextField slugField = new TextField("Slug");
    private final TextField imageField = new TextField("Image");
    private final IntegerField xpField = new IntegerField("XP");
    private final TextArea notesField = new TextArea("Notes");

    @Autowired
    public CharacterDetailView(
            CharacterRepository characterRepository,
            VallterraUserRepository vallterraUserRepository,
            AdventureRepository adventureRepository,
            CharacterAdventureRepository characterAdventureRepository) {
        this.characterRepository = characterRepository;
        this.vallterraUserRepository = vallterraUserRepository;
        this.adventureRepository = adventureRepository;
        this.characterAdventureRepository = characterAdventureRepository;

        setSizeFull();

        // Configure form fields
        userComboBox.setWidthFull();
        userComboBox.setItemLabelGenerator(user -> user.getPlayerName() != null ? user.getPlayerName() : "Unknown");
        userComboBox.setItems(vallterraUserRepository.findAll());

        nameField.setWidthFull();
        raceField.setWidthFull();
        classField.setWidthFull();
        levelField.setWidthFull();
        backgroundField.setWidthFull();
        alignmentField.setWidthFull();
        languagesField.setWidthFull();
        speedField.setWidthFull();
        toolsField.setWidthFull();
        passivePerceptionField.setWidthFull();
        passiveInsightField.setWidthFull();
        initiativeField.setWidthFull();
        armorClassField.setWidthFull();
        bioField.setWidthFull();
        bioField.setHeight("150px");
        pointsField.setWidthFull();
        slugField.setWidthFull();
        imageField.setWidthFull();
        xpField.setWidthFull();
        notesField.setWidthFull();
        notesField.setHeight("150px");

        // Configure adventures grid
        configureAdventuresGrid();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer characterId) {
        removeAll();

        if (characterId != null) {
            // Edit existing character
            Optional<Character> characterOpt = characterRepository.findById(characterId);
            if (characterOpt.isPresent()) {
                character = characterOpt.get();
                fillCharacterForm(character);
                loadAdventures(character);
            } else {
                // Character not found, redirect to characters view
                getUI().ifPresent(ui -> ui.navigate(CharactersView.class));
                return;
            }
        } else {
            // Create new character
            character = new Character();
            fillCharacterForm(character);
        }

        // Create layout
        var title = new H2(character.getId() == null ? "New Character" : "Edit Character: " + character.getName());

        // Create a layout with multiple columns for the form fields
        var column1 = new VerticalLayout(
                userComboBox, nameField, raceField, classField, levelField, backgroundField, alignmentField, languagesField, speedField, toolsField
        );
        column1.setSpacing(true);
        column1.setPadding(true);

        var column2 = new VerticalLayout(
                passivePerceptionField, passiveInsightField, initiativeField, armorClassField, bioField, pointsField, slugField, imageField, xpField, notesField
        );
        column2.setSpacing(true);
        column2.setPadding(true);

        var formLayout = new HorizontalLayout(column1, column2);
        formLayout.setWidthFull();

        var saveButton = new Button("Save", _ -> saveCharacter());
        var cancelButton = new Button("Cancel", _ -> getUI().ifPresent(ui -> ui.navigate(CharactersView.class)));
        var buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        var characterLayout = new VerticalLayout(title, formLayout, buttonLayout);
        characterLayout.setSpacing(true);

        if (character.getId() != null) {
            // Show adventures section for existing characters
            var adventuresTitle = new H3("Adventures");
            var adventuresHeader = new HorizontalLayout(adventuresTitle);
            adventuresHeader.setWidthFull();

            var adventuresLayout = new VerticalLayout(adventuresHeader, adventuresGrid);
            adventuresLayout.setSpacing(true);

            add(characterLayout, adventuresLayout);
        } else {
            add(characterLayout);
        }
    }

    private void fillCharacterForm(Character character) {
        if (character.getWikiUserId() != null) {
            vallterraUserRepository.findById(character.getWikiUserId()).ifPresent(userComboBox::setValue);
        }
        nameField.setValue(character.getName() != null ? character.getName() : "");
        raceField.setValue(character.getRace() != null ? character.getRace() : "");
        classField.setValue(character.getMainClass() != null ? character.getMainClass() : "");
        levelField.setValue(character.getLevel() != null ? character.getLevel() : 1);
        backgroundField.setValue(character.getBackground() != null ? character.getBackground() : "");
        alignmentField.setValue(character.getAlignment() != null ? character.getAlignment() : "");
        languagesField.setValue(character.getLanguages() != null ? character.getLanguages() : "");
        speedField.setValue(character.getSpeed() != null ? character.getSpeed() : 30);
        toolsField.setValue(character.getTools() != null ? character.getTools() : "");
        passivePerceptionField.setValue(character.getPassivePerception() != null ? character.getPassivePerception() : 10);
        passiveInsightField.setValue(character.getPassiveInsight() != null ? character.getPassiveInsight() : 10);
        initiativeField.setValue(character.getInitiative() != null ? character.getInitiative() : 0);
        armorClassField.setValue(character.getArmorClass() != null ? character.getArmorClass() : 10);
        bioField.setValue(character.getBio() != null ? character.getBio() : "");
        pointsField.setValue(character.getPoints() != null ? character.getPoints() : 0);
        slugField.setValue(character.getSlug() != null ? character.getSlug() : "");
        imageField.setValue(character.getImage() != null ? character.getImage() : "");
        xpField.setValue(character.getXp() != null ? character.getXp() : 0);
        notesField.setValue(character.getNotes() != null ? character.getNotes() : "");
    }

    private void saveCharacter() {
        VallterraUser selectedUser = userComboBox.getValue();
        if (selectedUser != null) {
            character.setWikiUserId(selectedUser.getId());
        }

        character.setName(nameField.getValue());
        character.setRace(raceField.getValue());
        character.setMainClass(classField.getValue());
        character.setLevel(levelField.getValue());
        character.setBackground(backgroundField.getValue());
        character.setAlignment(alignmentField.getValue());
        character.setLanguages(languagesField.getValue());
        character.setSpeed(speedField.getValue());
        character.setTools(toolsField.getValue());
        character.setPassivePerception(passivePerceptionField.getValue());
        character.setPassiveInsight(passiveInsightField.getValue());
        character.setInitiative(initiativeField.getValue());
        character.setArmorClass(armorClassField.getValue());
        character.setBio(bioField.getValue());
        character.setPoints(pointsField.getValue());
        character.setSlug(slugField.getValue());
        character.setImage(imageField.getValue());
        character.setXp(xpField.getValue());
        character.setNotes(notesField.getValue());

        characterRepository.save(character);
        getUI().ifPresent(ui -> ui.navigate(CharactersView.class));
    }

    private void configureAdventuresGrid() {
        adventuresGrid.setSizeFull();
        adventuresGrid.setColumns("id", "adventureId", "notes", "inParty");
        adventuresGrid.getColumnByKey("id").setHeader("ID");
        adventuresGrid.getColumnByKey("adventureId").setHeader("Adventure ID");
        adventuresGrid.getColumnByKey("notes").setHeader("Notes");
        adventuresGrid.getColumnByKey("inParty").setHeader("In Party");

        // Add a column with adventure details
        adventuresGrid.addComponentColumn(characterAdventure -> {
            Optional<Adventure> adventure = adventureRepository.findById(characterAdventure.getAdventureId());
            return new TextField(adventure.map(Adventure::getSlug).orElse("Unknown"));
        }).setHeader("Adventure").setAutoWidth(true);

        adventuresGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        adventuresGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                // Navigate to adventure detail view
                getUI().ifPresent(ui -> ui.navigate(AdventureDetailView.class, event.getValue().getAdventureId()));
            }
        });
    }

    private void loadAdventures(Character character) {
        if (character.getId() != null) {
            adventuresGrid.setItems(characterAdventureRepository.findByCharacterId(character.getId()));
        }
    }
}
