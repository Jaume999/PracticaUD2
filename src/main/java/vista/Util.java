package vista;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Util {
	public static void mensajeExcepcion(Exception ex, String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error de excepción");
        alert.setHeaderText(msg);
        alert.setContentText(ex.getMessage());

        String exceptionText = "";
        StackTraceElement[] stackTrace = ex.getStackTrace();
        for (StackTraceElement ste : stackTrace) {
            exceptionText = exceptionText + ste.toString() + System.getProperty("line.separator");
        }

        Label label = new Label("La traza de la excepción ha sido: ");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
