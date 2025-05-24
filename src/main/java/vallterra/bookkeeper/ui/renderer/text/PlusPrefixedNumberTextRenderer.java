package vallterra.bookkeeper.ui.renderer.text;

import com.vaadin.flow.data.renderer.TextRenderer;

import java.util.function.Function;

public class PlusPrefixedNumberTextRenderer<T, V extends Number> extends TextRenderer<T> {

    public PlusPrefixedNumberTextRenderer(Function<T, V> itemLabelGenerator) {
        super(v -> {
            var number = itemLabelGenerator.apply(v);

            if (number == null) {
                return null;
            }

            return number.doubleValue() > 0 ? "+" + number : String.valueOf(number);
        });
    }

}
