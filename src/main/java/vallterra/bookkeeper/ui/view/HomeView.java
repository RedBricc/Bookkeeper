package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import vallterra.bookkeeper.ui.MainLayout;

@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new Text("Welcome to the home view."));
    }

}