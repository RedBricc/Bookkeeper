package vallterra.bookkeeper.ui.component;

public class VallterraExternalLink extends ExternalLink {

    public VallterraExternalLink(String path) {
        this(path, "https://vallterra.wiki");
    }

    public VallterraExternalLink(String path, String urlBase) {
        super(urlBase + path);

        setText(path);
        setTarget("_blank");
    }

}
