package vallterra.bookkeeper.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;

import java.util.Objects;

public class LabeledFormItem extends FormLayout.FormItem {

    public LabeledFormItem(String label, Component value) {
        super(value);
        addToLabel(new NativeLabel(label));
    }

    public LabeledFormItem(String label, String value) {
        this(label, new Text(value));
    }

    public LabeledFormItem(String label, Object value) {
        this(label, Objects.toString(value));
    }

}
