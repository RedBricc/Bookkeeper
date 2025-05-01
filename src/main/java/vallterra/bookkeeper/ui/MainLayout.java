package vallterra.bookkeeper.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import vallterra.bookkeeper.ui.view.*;

public class MainLayout extends AppLayout {

    private final transient AuthenticationContext authContext;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;

        buildNavbar();
        buildDrawer();
    }

    private void buildNavbar() {
        var bookIcon = VaadinIcon.BOOK.create();
        bookIcon.getStyle().setFontSize("1.25rem");

        var titleContainer = new HorizontalLayout(bookIcon, new H2("Bookkeeper"));
        titleContainer.setPadding(true);
        titleContainer.setAlignItems(FlexComponent.Alignment.CENTER);

        addToNavbar(titleContainer);
    }

    private void buildDrawer() {
        var user = authContext.getAuthenticatedUser(UserDetails.class);

        var drawerContainer = new VerticalLayout();
        drawerContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        drawerContainer.setSizeFull();

        var pages = new SideNav();

        pages.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
        pages.addItem(new SideNavItem("Quests", QuestsView.class, VaadinIcon.ABACUS.create()));
        pages.addItem(new SideNavItem("Adventures", AdventuresView.class, VaadinIcon.ROAD.create()));
        pages.addItem(new SideNavItem("Characters", CharactersView.class, VaadinIcon.USERS.create()));
        pages.addItem(new SideNavItem("DM Tools", DmToolsView.class, VaadinIcon.TOOLS.create()));
        pages.addItem(new SideNavItem("Notes", NotesView.class, VaadinIcon.PENCIL.create()));

        drawerContainer.add(pages);

        if (user.isPresent()) {
            var loggedUser = new Span("Welcome %s!".formatted(user.get().getUsername()));
            var logout = new Button("Logout", click -> this.authContext.logout());

            drawerContainer.add(new VerticalLayout(loggedUser, logout));
        }

        addToDrawer(drawerContainer);
    }
}
