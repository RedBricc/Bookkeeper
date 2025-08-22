package vallterra.bookkeeper.ui.component;

import com.vaadin.flow.component.html.Anchor;

public class ExternalLink extends Anchor {

    public ExternalLink(String path) {
        this(path, "");
    }

    public ExternalLink(String path, String urlBase) {
        super(urlBase + path);

        setText(path);
        setTarget("_blank");
    }

}
