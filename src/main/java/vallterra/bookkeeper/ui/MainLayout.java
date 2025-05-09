package vallterra.bookkeeper.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import vallterra.bookkeeper.ui.view.CharactersView;
import vallterra.bookkeeper.ui.view.HomeView;
import vallterra.bookkeeper.ui.view.PointRewardsView;
import vallterra.bookkeeper.ui.view.QuestsView;

public class MainLayout extends AppLayout {

    private final transient AuthenticationContext authContext;
    private final SplitLayout contentLayout;
    private Component content;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;

        buildNavbar();

        contentLayout = new SplitLayout();
        contentLayout.setSizeFull();
        contentLayout.setSplitterPosition(20);
        contentLayout.addToPrimary(buildSidebar());

        super.setContent(contentLayout);
    }

    @Override
    public void setContent(Component content) {
        var splitterPosition = contentLayout.getSplitterPosition();

        removeContent();
        if (content != null) {
            this.content = content;
            content.getElement().removeAttribute("slot");

            contentLayout.addToSecondary(content);
        }

        contentLayout.setSplitterPosition(splitterPosition);
    }

    private void removeContent() {
        if (this.content != null) {
            this.content.getElement().removeFromParent();
            this.content = null;
        }
    }

    private void buildNavbar() {
        var user = authContext.getAuthenticatedUser(UserDetails.class);

        var headerContainer = new HorizontalLayout();
        headerContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContainer.setAlignItems(FlexComponent.Alignment.END);
        headerContainer.setPadding(true);
        headerContainer.setWidthFull();

        headerContainer.add(getTitleContainer(), getPages(), getUserContainer(user.orElse(null)));

        addToNavbar(headerContainer);
    }

    private static HorizontalLayout getTitleContainer() {
        var bookIcon = VaadinIcon.BOOK.create();
        bookIcon.getStyle().setFontSize("1.25rem");

        var titleContainer = new HorizontalLayout(bookIcon, new H2("Bookkeeper"));
        titleContainer.setPadding(false);
        titleContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        titleContainer.getStyle().set("flex", "1");

        return titleContainer;
    }

    private HorizontalLayout getPages() {
        var pages = new HorizontalLayout();
        pages.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        pages.getStyle().set("flex", "1");

        pages.add(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
        pages.add(new SideNavItem("Quests", QuestsView.class, VaadinIcon.ROAD.create()));
        pages.add(new SideNavItem("Characters", CharactersView.class, VaadinIcon.USERS.create()));
        pages.add(new SideNavItem("Rewards", PointRewardsView.class, VaadinIcon.TROPHY.create()));

        return pages;
    }

    private HorizontalLayout getUserContainer(@Nullable UserDetails user) {
        var userContainer = new HorizontalLayout();

        if (user != null) {
            var loggedUser = new Span(user.getUsername());
            var logout = new Button("Logout", _ -> this.authContext.logout());
            logout.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

            userContainer.add(loggedUser, logout);
        }

        userContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        userContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        userContainer.getStyle().set("flex", "1");

        return userContainer;
    }

    private VerticalLayout buildSidebar() {
        var sideBarLayout = new VerticalLayout();
        sideBarLayout.setWidthFull();

        sideBarLayout.add(buildOpenedTool("Quest Outline", "Create and manage quest outlines"));
        sideBarLayout.add(buildDisabledTool("Combat Tracker", "Track combat encounters"));
        sideBarLayout.add(buildTool("Location Tracker", "Track locations and maps"));
        sideBarLayout.add(buildTool("NPC Tracker", "Track NPCs in the current location"));
        sideBarLayout.add(buildDisabledTool("Quest Log", "Log for the current quest"));
        sideBarLayout.add(buildTool("Story beats", "Track story beats and plot points"));
        sideBarLayout.add(buildOpenedTool("Reminders", "Set reminders in a to-do list"));

        return sideBarLayout;
    }

    private Details buildOpenedTool(String title, String description) {
        var tool = buildTool(title, description);

        tool.setOpened(true);

        return tool;
    }

    private Details buildDisabledTool(String title, String description) {
        var tool = buildTool(title, description);

        tool.setEnabled(false);

        return tool;
    }

    private Details buildTool(String title, String description) {
        var tool = new Details(title, new Span(description));

        tool.addThemeVariants(DetailsVariant.REVERSE);
        tool.setWidthFull();

        return tool;
    }

}
