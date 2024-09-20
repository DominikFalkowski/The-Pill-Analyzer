module org.example.pillanalyzer20102969 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.swing;
    requires jdk.compiler;
    requires annotations;

    opens org.example.pillanalyzer20102969 to javafx.fxml;
    exports org.example.pillanalyzer20102969;
}