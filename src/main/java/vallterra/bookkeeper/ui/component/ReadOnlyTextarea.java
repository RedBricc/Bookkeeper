package vallterra.bookkeeper.ui.component;

import com.vaadin.flow.component.textfield.TextArea;

import java.util.Objects;

public class ReadOnlyTextarea extends TextArea {

    public ReadOnlyTextarea(Object value) {
        setValue(Objects.toString(value, ""));
        setReadOnly(true);
        setWidthFull();
        addClassName("bookkeeper-text-area");
    }

    public ReadOnlyTextarea(Object value, String label) {
        this(value);
        setLabel(label);
    }

}
