package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import vallterra.bookkeeper.ui.MainLayout;

@Route(value = "dm-tools", layout = MainLayout.class)
@PermitAll
public class DmToolsView extends VerticalLayout {

    public DmToolsView() {
        add(new Text("Welcome to the DM tools view."));
    }

}