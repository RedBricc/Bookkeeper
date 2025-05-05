package vallterra.bookkeeper.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(value = "bookkeeper", variant = Lumo.DARK)
@Push(value = PushMode.AUTOMATIC)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/bookkeeper-grid.css", themeFor = "vaadin-grid")
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setViewport("width=device-width, initial-scale=1");
        settings.setPageTitle("Bookkeeper");
        settings.setBodySize("100vw", "100vh");
        settings.addMetaTag("author", "RedBricc");
    }
}
