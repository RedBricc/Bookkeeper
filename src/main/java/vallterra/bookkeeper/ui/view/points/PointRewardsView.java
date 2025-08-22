package vallterra.bookkeeper.ui.view.points;

import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.jooq.generated.tables.records.PointRewardRecord;
import vallterra.bookkeeper.ui.MainLayout;
import vallterra.bookkeeper.ui.component.grid.BookkeeperGridLayout;
import vallterra.bookkeeper.ui.data.ContextAccess;

import java.util.List;

import static org.jooq.generated.tables.PointReward.POINT_REWARD;

@Route(value = "point-rewards", layout = MainLayout.class)
@PermitAll
public class PointRewardsView extends BookkeeperGridLayout<PointRewardRecord> {
    public PointRewardsView(ContextAccess contextAccess) {
        super(POINT_REWARD, contextAccess, "Point Rewards");
        setSizeFull();

        configureGrid();
        configureToolbar();
    }

    private void configureGrid() {
        grid().addFrozenColumn(POINT_REWARD.ID, true);
        var costColumn = grid().addFixedSizeColumn(POINT_REWARD.POINT_COST);
        grid().addColumn(POINT_REWARD.REWARD);
        grid().addTextAreaColumn(POINT_REWARD.DESCRIPTION);
        grid().addColumn(POINT_REWARD.DURATION);
        grid().addFixedSizeColumn(POINT_REWARD.XP_GAINED);

        grid().sort(List.of(new GridSortOrder<>(costColumn, SortDirection.ASCENDING)));
    }

    private void configureToolbar() {
        // TODO: Add toolbar components
    }
}
