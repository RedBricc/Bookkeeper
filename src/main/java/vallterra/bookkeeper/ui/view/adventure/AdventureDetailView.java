package vallterra.bookkeeper.ui.view.adventure;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import org.jooq.generated.tables.pojos.Quest;
import org.springframework.beans.factory.annotation.Autowired;
import vallterra.bookkeeper.backend.adventure.AdventureRepository;
import vallterra.bookkeeper.backend.adventure.CharacterAdventureRepository;
import vallterra.bookkeeper.backend.quest.QuestRepository;
import vallterra.bookkeeper.backend.user.CharacterRepository;
import vallterra.bookkeeper.ui.MainLayout;

import java.time.LocalDateTime;
import java.util.Optional;

@Route(value = "adventure", layout = MainLayout.class)
@PermitAll
public class AdventureDetailView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final AdventureRepository adventureRepository;
    private final QuestRepository questRepository;
    private final CharacterRepository characterRepository;
    private final CharacterAdventureRepository characterAdventureRepository;

    private Adventure adventure;
    private final Grid<CharacterAdventure> charactersGrid = new Grid<>(CharacterAdventure.class);

    // Form fields
    private final ComboBox<Quest> questComboBox = new ComboBox<>("Quest");
    private final TextField slugField = new TextField("Slug");
    private final TextArea notesField = new TextArea("Notes");
    private final TextField mapField = new TextField("Map");

    @Autowired
    public AdventureDetailView(
            AdventureRepository adventureRepository,
            QuestRepository questRepository,
            CharacterRepository characterRepository,
            CharacterAdventureRepository characterAdventureRepository) {
        this.adventureRepository = adventureRepository;
        this.questRepository = questRepository;
        this.characterRepository = characterRepository;
        this.characterAdventureRepository = characterAdventureRepository;

        setSizeFull();
        
        // Configure form fields
        questComboBox.setWidthFull();
        questComboBox.setItemLabelGenerator(Quest::getName);
        questComboBox.setItems(questRepository.findAll());
        
        slugField.setWidthFull();
        notesField.setWidthFull();
        notesField.setHeight("150px");
        mapField.setWidthFull();
        
        // Configure characters grid
        configureCharactersGrid();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer adventureId) {
        removeAll();
        
        if (adventureId != null) {
            // Edit existing adventure
            Optional<Adventure> adventureOpt = adventureRepository.findById(adventureId);
            if (adventureOpt.isPresent()) {
                adventure = adventureOpt.get();
                fillAdventureForm(adventure);
                loadCharacters(adventure);
            } else {
                // Adventure not found, redirect to adventures view
                return;
            }
        } else {
            // Create new adventure
            adventure = new Adventure();
            adventure.setCreatedAt(LocalDateTime.now());
            adventure.setUpdatedAt(LocalDateTime.now());
            fillAdventureForm(adventure);
        }
        
        // Create layout
        var title = new H2(adventure.getId() == null ? "New Adventure" : "Edit Adventure: " + adventure.getSlug());
        
        var formLayout = new VerticalLayout(
                questComboBox, slugField, notesField, mapField
        );
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        
        var saveButton = new Button("Save", _ -> saveAdventure());
        var buttonLayout = new HorizontalLayout(saveButton);

        var adventureLayout = new VerticalLayout(title, formLayout, buttonLayout);
        adventureLayout.setSpacing(true);
        
        if (adventure.getId() != null) {
            // Show characters section for existing adventures
            var charactersTitle = new H3("Characters");
            var addCharacterButton = new Button("Add Character", VaadinIcon.PLUS.create(), _ -> addCharacterDialog());
            var charactersHeader = new HorizontalLayout(charactersTitle, addCharacterButton);
            charactersHeader.setWidthFull();
            charactersHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
            
            var charactersLayout = new VerticalLayout(charactersHeader, charactersGrid);
            charactersLayout.setSpacing(true);
            
            add(adventureLayout, charactersLayout);
        } else {
            add(adventureLayout);
        }
    }
    
    private void fillAdventureForm(Adventure adventure) {
        if (adventure.getQuestId() != null) {
            questRepository.findById(adventure.getQuestId()).ifPresent(questComboBox::setValue);
        }
        slugField.setValue(adventure.getSlug() != null ? adventure.getSlug() : "");
        notesField.setValue(adventure.getNotes() != null ? adventure.getNotes() : "");
        mapField.setValue(adventure.getMap() != null ? adventure.getMap() : "");
    }
    
    private void saveAdventure() {
        Quest selectedQuest = questComboBox.getValue();
        if (selectedQuest != null) {
            adventure.setQuestId(selectedQuest.getId());
        }
        
        adventure.setSlug(slugField.getValue());
        adventure.setNotes(notesField.getValue());
        adventure.setMap(mapField.getValue());
        adventure.setUpdatedAt(LocalDateTime.now());
        
        if (adventure.getCreatedAt() == null) {
            adventure.setCreatedAt(LocalDateTime.now());
        }
        
        adventureRepository.save(adventure);
    }
    
    private void configureCharactersGrid() {
        charactersGrid.setSizeFull();
        charactersGrid.setColumns("id", "characterId", "notes", "inParty");
        charactersGrid.getColumnByKey("id").setHeader("ID");
        charactersGrid.getColumnByKey("characterId").setHeader("Character ID");
        charactersGrid.getColumnByKey("notes").setHeader("Notes");
        charactersGrid.getColumnByKey("inParty").setHeader("In Party");
        
        // Add a column with character name
        charactersGrid.addComponentColumn(characterAdventure -> {
            Optional<Character> character = characterRepository.findById(characterAdventure.getCharacterId());
            return new TextField(character.map(Character::getName).orElse("Unknown"));
        }).setHeader("Character Name").setAutoWidth(true);
        
        // Add a remove button column
        charactersGrid.addComponentColumn(characterAdventure -> {
            Button removeButton = new Button("Remove", VaadinIcon.TRASH.create());
            removeButton.addClickListener(_ -> {
                characterAdventureRepository.deleteById(characterAdventure.getId());
                loadCharacters(adventure);
            });
            return removeButton;
        }).setHeader("Actions").setAutoWidth(true);
        
        charactersGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate("character/" + event.getValue().getCharacterId()));
            }
        });
    }
    
    private void loadCharacters(Adventure adventure) {
        if (adventure.getId() != null) {
            charactersGrid.setItems(characterAdventureRepository.findByAdventureId(adventure.getId()));
        }
    }
    
    private void addCharacterDialog() {
        // TODO: Implement character selection dialog
    }
}