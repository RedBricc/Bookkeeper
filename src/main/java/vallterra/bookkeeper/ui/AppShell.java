package vallterra.bookkeeper.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(variant = Lumo.DARK)
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setViewport("width=device-width, initial-scale=1");
        settings.setPageTitle("Bookkeeper");
        settings.setBodySize("100vw", "100vh");
        settings.addMetaTag("author", "RedBricc");
    }
}
