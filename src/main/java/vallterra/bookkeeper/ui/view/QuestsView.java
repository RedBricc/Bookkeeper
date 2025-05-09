package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.generated.tables.records.VAdventureRecord;
import org.jooq.generated.tables.records.VQuestRecord;
import vallterra.bookkeeper.ui.MainLayout;
import vallterra.bookkeeper.ui.component.grid.BookkeeperGridLayout;

import java.util.List;

import static org.jooq.generated.tables.VAdventure.V_ADVENTURE;
import static org.jooq.generated.tables.VQuest.V_QUEST;

@Route(value = "quests", layout = MainLayout.class)
@PermitAll
public class QuestsView extends BookkeeperGridLayout<VQuestRecord> {

    public QuestsView() {
        super(V_QUEST, "Quests");
        setSizeFull();

        configureGrid();
        configureToolbar();

        grid().setDetailsVisibleOnClick(false);
        grid().setItemDetailsRenderer(new ComponentRenderer<>(this::createDetailsLayout));
        grid().setPartNameGenerator(quest -> {
            if (quest.getCompletedAt() != null) {
                return "completed";
            } else if (quest.getAdventureCount() != null) {
                return "started";
            } else {
                return "not-started";
            }
        });
    }

    private void configureGrid() {
        var idColumn = grid().addDetailsToggleColumn(V_QUEST.ID, true);
        grid().addRouteColumn(V_QUEST.NAME, V_QUEST.ID, QuestDetailView.class);
        grid().addColumn(V_QUEST.LOCATION, true); // TODO: Add location view link
        grid().addFixedSizeColumn(V_QUEST.DIFFICULTY, true).setWidth("100px");
        grid().addFixedSizeColumn(V_QUEST.ADVENTURE_COUNT);
        grid().addFixedRouteColumn(V_QUEST.POINTS, PointRewardsView.class);
        grid().addColumn(V_QUEST.CONTACT); // TODO: Add NPC view link
        grid().addVallterraLinkColumn(V_QUEST.LOCATION_LINK);
        grid().addColumn(V_QUEST.COMPLETED_AT);

        grid().sort(List.of(new GridSortOrder<>(idColumn, SortDirection.DESCENDING)));
    }

    private void configureToolbar() {
        var newQuestButton = new Button("New Quest", VaadinIcon.PLUS.create());
        newQuestButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addActionComponent(newQuestButton);
    }

    private BookkeeperGridLayout<VAdventureRecord> createDetailsLayout(VQuestRecord quest) {
        var detailsLayout = new BookkeeperGridLayout<>(V_ADVENTURE, "Adventures", false, DisplayMode.DETAILS);
        detailsLayout.setSizeFull();

        detailsLayout.grid().addFixedCondition(V_ADVENTURE.QUEST_ID.eq(quest.getId()));

        detailsLayout.grid().addRouteColumn(V_ADVENTURE.CREATED_AT, V_ADVENTURE.ID, AdventureDetailView.class);
        detailsLayout.grid().addFixedSizeColumn(V_ADVENTURE.PARTY_SIZE);
        detailsLayout.grid().addTextAreaColumn(V_ADVENTURE.NOTES);
        detailsLayout.grid().addVallterraLinkColumn(V_ADVENTURE.SLUG, "/adventure/").setHeader("Wiki Link");
        detailsLayout.grid().addVallterraLinkColumn(V_ADVENTURE.MAP_PATH, "").setHeader("Map Link");

        return detailsLayout;
    }
}
