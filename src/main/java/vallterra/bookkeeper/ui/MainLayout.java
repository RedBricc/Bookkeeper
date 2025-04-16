package vallterra.bookkeeper.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import vallterra.bookkeeper.ui.view.CharactersView;
import vallterra.bookkeeper.ui.view.DmToolsView;
import vallterra.bookkeeper.ui.view.HomeView;
import vallterra.bookkeeper.ui.view.NotesView;

public class MainLayout extends AppLayout {

    private final transient AuthenticationContext authContext;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;

        buildNavbar();
        buildDrawer();
    }

    private void buildNavbar() {
        var bookIcon = VaadinIcon.BOOK.create();
        bookIcon.getStyle().setFontSize("1.5rem");

        var titleContainer = new HorizontalLayout(bookIcon, new H1("Bookkeeper"));
        titleContainer.setPadding(true);
        titleContainer.setAlignItems(FlexComponent.Alignment.CENTER);

        addToNavbar(titleContainer);
    }

    private void buildDrawer() {
        var user = authContext.getAuthenticatedUser(UserDetails.class);

        var drawerContainer = new VerticalLayout();
        drawerContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        drawerContainer.setSizeFull();

        var pages = new VerticalLayout();
        pages.addClassName("container-large");

        pages.add(createMenuLink(HomeView.class, "Home", VaadinIcon.HOME));
        pages.add(createMenuLink(CharactersView.class, "Characters", VaadinIcon.USERS));
        pages.add(createMenuLink(NotesView.class, "Notes", VaadinIcon.PENCIL));
        pages.add(createMenuLink(DmToolsView.class, "DM Tools", VaadinIcon.TOOLS));

        drawerContainer.add(pages);

        if (user.isPresent()) {
            var loggedUser = new Span("Welcome %s!".formatted(user.get().getUsername()));
            var logout = new Button("Logout", click -> this.authContext.logout());

            drawerContainer.add(new VerticalLayout(loggedUser, logout));
        }

        addToDrawer(drawerContainer);
    }

    private RouterLink createMenuLink(Class<? extends Component> viewClass, String caption, VaadinIcon icon) {
        var routerLink = new RouterLink(viewClass);

        routerLink.setClassName("menu-link");
        routerLink.add(icon.create());
        routerLink.add(new Span(caption));

        return routerLink;
    }
}
