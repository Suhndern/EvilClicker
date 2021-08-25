package com.github.suhndern.evilclicker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EvilClickerController {
    @FXML
    public Label cookieCounterLabel;
    @FXML
    public Button cookieButton;
    @FXML
    public Hyperlink gitHubSocialHyperlink;

    AtomicBoolean evilButton = new AtomicBoolean();
    AtomicInteger cookieCounter = new AtomicInteger();
    AtomicInteger guaranteedSafeClicks = new AtomicInteger();

    ScheduledExecutorService scheduler;

    private boolean getButtonEvilness() {
        return evilButton.get();
    }

    private void resetButtonEvilness() {
        evilButton.set(false);
    }

    private boolean updateButtonEvilnessAndGet() {
        int maxBound = 10;
        Random random = new Random();

        // determine if button is evil
        evilButton.set(random.nextInt(maxBound) == 1);

        return evilButton.get();
    }

    private void updateButtonIcon() {
        if (getButtonEvilness()) {
            cookieButton.setText("\uD83D\uDD01");
        } else {
            cookieButton.setText("\uD83C\uDF6A");
        }
    }

    private void resetButton() {
        resetButtonEvilness();
        resetSafeClicks();
        Platform.runLater(this::updateButtonIcon);
    }

    private void scheduleButtonReset() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = this::resetButton;
        int delay = 2;

        scheduler.schedule(task, delay, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    private void incrementCounter() {
        cookieCounter.incrementAndGet();
    }

    private void resetCounter() {
        cookieCounter.set(0);
    }

    private void updateCookieCounterLabel() {
        cookieCounterLabel.setText("Cookies: " + cookieCounter.toString());
    }

    private int getSafeClicks() {
        return guaranteedSafeClicks.get();
    }

    private void decrementSafeClicks() {
        guaranteedSafeClicks.decrementAndGet();
        guaranteedSafeClicks.compareAndSet(-1, 0);
    }

    private void resetSafeClicks() {
        int minBound = 20;
        int maxBound = 100;
        guaranteedSafeClicks.set(new Random().nextInt(maxBound - minBound) + minBound);
    }

    private void initializeGitHubSocialHyperlink() {
        ImageView imageView = new ImageView();
        Image image = new Image(Objects.requireNonNull(EvilClickerController.class.getResourceAsStream("GitHub-Mark-32px.png")));

        imageView.setImage(image);
        imageView.setFitWidth(22);
        imageView.setFitHeight(22);

        gitHubSocialHyperlink.setGraphic(imageView);

        gitHubSocialHyperlink.hoverProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                gitHubSocialHyperlink.setEffect(new ColorAdjust(0, 0, 0, -1));
            } else {
                gitHubSocialHyperlink.setEffect(new ColorAdjust());
            }
        }));
    }

    @FXML
    private void initialize() {
        resetSafeClicks();
        updateCookieCounterLabel();
        initializeGitHubSocialHyperlink();
    }

    @FXML
    private void onCookieButtonClick() {
        if (evilButton.get()) {
            scheduler.shutdownNow();
            resetButton();
            resetCounter();
        } else {
            incrementCounter();
            decrementSafeClicks();

            if (getSafeClicks() == 0 && updateButtonEvilnessAndGet()) {
                scheduleButtonReset();
            }
        }

        updateCookieCounterLabel();
        updateButtonIcon();
    }

    @FXML
    private void onGitHubSocialHyperlinkClick() {
        new EvilClickerApplication().getHostServices().showDocument("https://www.github.com/Suhndern/EvilClicker");
    }
}