package vallterra.bookkeeper.ui.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import vallterra.bookkeeper.ui.MainLayout;

@Route(value = "characters", layout = MainLayout.class)
@PermitAll
public class CharactersView extends VerticalLayout {

    public CharactersView() {
        add(new Text("Welcome to the characters view."));
    }

}