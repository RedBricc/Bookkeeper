package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.button.Button;
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
import org.jooq.generated.tables.pojos.Quest;
import org.springframework.beans.factory.annotation.Autowired;
import vallterra.bookkeeper.backend.adventure.AdventureRepository;
import vallterra.bookkeeper.backend.quest.QuestRepository;
import vallterra.bookkeeper.ui.MainLayout;

import java.util.Optional;

@Route(value = "quest", layout = MainLayout.class)
@PermitAll
public class QuestDetailView extends VerticalLayout implements HasUrlParameter<Integer> {

    private final QuestRepository questRepository;
    private final AdventureRepository adventureRepository;

    private Quest quest;
    private final Grid<Adventure> adventuresGrid = new Grid<>();

    // Form fields
    private final TextField nameField = new TextField("Name");
    private final TextField locationField = new TextField("Location");
    private final TextArea descriptionField = new TextArea("Description");
    private final TextArea dmNotesField = new TextArea("DM Notes");
    private final TextField pointsField = new TextField("Points");
    private final TextField contactField = new TextField("Contact");
    private final TextField contactLinkField = new TextField("Contact Link");
    private final TextField difficultyField = new TextField("Difficulty");
    private final TextField locationLinkField = new TextField("Location Link");

    @Autowired
    public QuestDetailView(QuestRepository questRepository, AdventureRepository adventureRepository) {
        this.questRepository = questRepository;
        this.adventureRepository = adventureRepository;

        setSizeFull();
        
        // Configure form fields
        nameField.setWidthFull();
        locationField.setWidthFull();
        descriptionField.setWidthFull();
        descriptionField.setHeight("150px");
        dmNotesField.setWidthFull();
        dmNotesField.setHeight("150px");
        pointsField.setWidthFull();
        contactField.setWidthFull();
        contactLinkField.setWidthFull();
        difficultyField.setWidthFull();
        locationLinkField.setWidthFull();
        
        // Configure adventures grid
        configureAdventuresGrid();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer questId) {
        removeAll();
        
        if (questId != null) {
            // Edit existing quest
            Optional<Quest> questOpt = questRepository.findById(questId);
            if (questOpt.isPresent()) {
                quest = questOpt.get();
                fillQuestForm(quest);
                loadAdventures(quest);
            } else {
                // Quest not found, redirect to quests view
                getUI().ifPresent(ui -> ui.navigate(QuestsView.class));
                return;
            }
        } else {
            // Create new quest
            quest = new Quest();
            fillQuestForm(quest);
        }
        
        // Create layout
        var title = new H2(quest.getId() == null ? "New Quest" : "Edit Quest: " + quest.getName());
        
        var formLayout = new VerticalLayout(
                nameField, locationField, descriptionField, dmNotesField, 
                pointsField, contactField, contactLinkField, difficultyField, locationLinkField
        );
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        
        var saveButton = new Button("Save", e -> saveQuest());
        var cancelButton = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate(QuestsView.class)));
        var buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        
        var questLayout = new VerticalLayout(title, formLayout, buttonLayout);
        questLayout.setSpacing(true);
        
        if (quest.getId() != null) {
            // Show adventures section for existing quests
            var adventuresTitle = new H3("Adventures");
            var addAdventureButton = new Button("New Adventure", VaadinIcon.PLUS.create(), e -> createNewAdventure());
            var adventuresHeader = new HorizontalLayout(adventuresTitle, addAdventureButton);
            adventuresHeader.setWidthFull();
            adventuresHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
            
            var adventuresLayout = new VerticalLayout(adventuresHeader, adventuresGrid);
            adventuresLayout.setSpacing(true);
            
            add(questLayout, adventuresLayout);
        } else {
            add(questLayout);
        }
    }
    
    private void fillQuestForm(Quest quest) {
        nameField.setValue(quest.getName() != null ? quest.getName() : "");
        locationField.setValue(quest.getLocation() != null ? quest.getLocation() : "");
        descriptionField.setValue(quest.getDescription() != null ? quest.getDescription() : "");
        dmNotesField.setValue(quest.getDmNotes() != null ? quest.getDmNotes() : "");
        pointsField.setValue(quest.getPoints() != null ? quest.getPoints().toString() : "");
        contactField.setValue(quest.getContact() != null ? quest.getContact() : "");
        contactLinkField.setValue(quest.getContactLink() != null ? quest.getContactLink() : "");
        difficultyField.setValue(quest.getDifficulty() != null ? quest.getDifficulty() : "");
        locationLinkField.setValue(quest.getLocationLink() != null ? quest.getLocationLink() : "");
    }
    
    private void saveQuest() {
        quest.setName(nameField.getValue());
        quest.setLocation(locationField.getValue());
        quest.setDescription(descriptionField.getValue());
        quest.setDmNotes(dmNotesField.getValue());
        
        try {
            if (!pointsField.getValue().isEmpty()) {
                quest.setPoints(Integer.parseInt(pointsField.getValue()));
            }
        } catch (NumberFormatException e) {
            // Handle invalid number format
        }
        
        quest.setContact(contactField.getValue());
        quest.setContactLink(contactLinkField.getValue());
        quest.setDifficulty(difficultyField.getValue());
        quest.setLocationLink(locationLinkField.getValue());
        
        questRepository.save(quest);
        getUI().ifPresent(ui -> ui.navigate(QuestsView.class));
    }
    
    private void configureAdventuresGrid() {
        adventuresGrid.setSizeFull();
        adventuresGrid.addColumn(Adventure::getId).setHeader("ID").setWidth("90px").setFlexGrow(0);
        adventuresGrid.addColumn(Adventure::getSlug).setHeader("Slug");
        adventuresGrid.addColumn(Adventure::getCreatedAt).setHeader("Created At");
        adventuresGrid.addColumn(Adventure::getUpdatedAt).setHeader("Updated At");
        
        adventuresGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                getUI().ifPresent(ui -> ui.navigate("adventure/" + event.getValue().getId()));
            }
        });
    }
    
    private void loadAdventures(Quest quest) {
        if (quest.getId() != null) {
            adventuresGrid.setItems(adventureRepository.findByQuestId(quest.getId().longValue()));
        }
    }
    
    private void createNewAdventure() {
        return; // TODO: Implement adventure creation logic
    }
}