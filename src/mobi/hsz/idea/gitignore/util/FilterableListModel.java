package mobi.hsz.idea.gitignore.util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterableListModel<T extends Comparable> extends DefaultListModel<T> {
    private List<T> elements;

    public void filter(String value) {
        List<T> filtered = new ArrayList<T>();
        for (T element : getElements()) {
            if (element.toString().toLowerCase().contains(value.toLowerCase())) {
                filtered.add(element);
            }
        }
        fill(filtered);
    }

    private void fill(List<T> elements) {
        removeAllElements();
        for (T element : elements) {
            addElement(element);
        }
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        Collections.sort(elements);
        this.elements = elements;
        fill(elements);
    }
}
