module co.edu.uniquindio.poo.proyectofinalprgii {
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
    requires com.fasterxml.jackson.annotation;
    requires java.desktop;
    requires jakarta.mail;

    opens co.edu.uniquindio.poo.proyectofinalprgii to javafx.fxml;
    exports co.edu.uniquindio.poo.proyectofinalprgii;
    exports co.edu.uniquindio.poo.proyectofinalprgii.Application;
    opens co.edu.uniquindio.poo.proyectofinalprgii.Application to javafx.fxml;
    exports co.edu.uniquindio.poo.proyectofinalprgii.Controller;
    opens co.edu.uniquindio.poo.proyectofinalprgii.Controller to javafx.fxml;
}