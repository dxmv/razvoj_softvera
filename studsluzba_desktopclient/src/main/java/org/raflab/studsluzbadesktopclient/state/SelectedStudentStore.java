package org.raflab.studsluzbadesktopclient.state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.springframework.stereotype.Component;

/**
 * Keeps the currently selected student so multiple views (search, profile, etc.)
 * can exchange context without tightly coupling controllers.
 */
@Component
public class SelectedStudentStore {

    public static class Selection {
        private final StudentDto student;
        private final String index;

        public Selection(StudentDto student, String index) {
            this.student = student;
            this.index = index;
        }

        public StudentDto getStudent() {
            return student;
        }

        public String getIndex() {
            return index;
        }
    }

    private final ObjectProperty<Selection> selection = new SimpleObjectProperty<>();

    public ObjectProperty<Selection> selectionProperty() {
        return selection;
    }

    public Selection getSelection() {
        return selection.get();
    }

    public void select(StudentDto student) {
        selection.set(new Selection(student, null));
    }

    public void select(StudentDto student, String index) {
        selection.set(new Selection(student, index));
    }

    public void clear() {
        selection.set(null);
    }
}
