package com.pharmacy.org.pharmacy;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        CSSFX.start();

        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();
        stage.initStyle(StageStyle.UNDECORATED);
        SceneManager.setPrimaryStage(stage);
        SceneManager.loadScene("main-view.fxml", "Login");
    }

    public static void main(String[] args) {
        launch();
    }
}
