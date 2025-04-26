package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import vallterra.bookkeeper.ui.MainLayout;

@Route(value = "quests", layout = MainLayout.class)
@PermitAll
public class QuestsView extends VerticalLayout {

    public QuestsView() {
        add(new Text("Welcome to the quests view."));
    }

}