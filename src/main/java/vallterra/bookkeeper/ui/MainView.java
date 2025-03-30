package vallterra.bookkeeper.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(layout = MainLayout.class)
@PermitAll
public class MainView extends VerticalLayout {

    public MainView() {
        add(new Text("Welcome to MainView."));
    }

}