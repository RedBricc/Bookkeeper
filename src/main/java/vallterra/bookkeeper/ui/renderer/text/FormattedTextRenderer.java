package vallterra.bookkeeper.ui.renderer.text;

import com.vaadin.flow.data.renderer.TextRenderer;

import java.util.function.Function;

public class FormattedTextRenderer<T> extends TextRenderer<T> {

    public FormattedTextRenderer(Function<T, ?> itemLabelGenerator, String format) {
        super( v -> {
            var label = itemLabelGenerator.apply(v);
            return label == null ? null : String.format(format, label);
        });
    }

}
