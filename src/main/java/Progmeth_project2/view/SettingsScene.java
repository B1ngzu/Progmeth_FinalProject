package Progmeth_project2.view;

import Progmeth_project2.controller.MenuController;
import Progmeth_project2.util.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Settings scene that lets the player toggle sound on/off and view
 * game instructions.
 */
public class SettingsScene extends BaseScene {

    // ── Controller ────────────────────────────────────────────────────────────

    private final MenuController controller;

    // ── UI nodes ─────────────────────────────────────────────────────────────

    private ToggleButton bgmToggle;
    private ToggleButton soundToggle;
    private Slider sfxVolumeSlider;
    private Button backButton;
    private Slider bgmVolumeSlider;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates the settings scene.
     *
     * @param stage      the application stage
     * @param controller the menu controller handling back navigation
     */
    public SettingsScene(Stage stage, MenuController controller) {
        super(stage);
        this.controller = controller;
    }

    // ── BaseScene lifecycle ───────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    protected void setupLayout() {
        VBox root = new VBox(28);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0D1B2A, #1A3A6B);");

        Label title = new Label("⚙  Settings");
        title.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-text-fill: white;"
                + "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.3), 10, 0.3, 0, 0);");

        // Sound toggle row
        Label soundLabel = new Label("Sound Effects & Music");
        soundLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");

        soundToggle = new ToggleButton(SoundManager.getInstance().isMuted() ? "OFF" : "ON");
        soundToggle.setSelected(!SoundManager.getInstance().isMuted());
        styleSoundToggle();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox soundRow = new HBox(20, soundLabel, spacer, soundToggle);
        soundRow.setAlignment(Pos.CENTER);
        soundRow.setPadding(new Insets(16));
        soundRow.setMaxWidth(500);
        soundRow.setStyle("-fx-background-color: rgba(255,255,255,0.07);"
                + "-fx-background-radius: 12;");

        // SFX volume row
        Label sfxLabel = new Label("SFX Volume");
        sfxLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");

        sfxVolumeSlider = new Slider(0.0, 1.0, SoundManager.getInstance().getSfxVolume());
        sfxVolumeSlider.setPrefWidth(240);
        sfxVolumeSlider.setStyle(
            "-fx-control-inner-background: #1A3A6B;"
          + "-fx-accent: #2980B9;"
        );

        // BGM toggle row
        Label bgmLabel = new Label("Background Music");
        bgmLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");

        bgmToggle = new ToggleButton(SoundManager.getInstance().isBgMuted() ? "OFF" : "ON");
        bgmToggle.setSelected(!SoundManager.getInstance().isBgMuted());
        styleBgmToggle();

        Region bgmSpacer = new Region();
        HBox.setHgrow(bgmSpacer, Priority.ALWAYS);
        HBox bgmRow = new HBox(20, bgmLabel, bgmSpacer, bgmToggle);
        bgmRow.setAlignment(Pos.CENTER);
        bgmRow.setPadding(new Insets(16));
        bgmRow.setMaxWidth(500);
        bgmRow.setStyle("-fx-background-color: rgba(255,255,255,0.07);"
                + "-fx-background-radius: 12;");

        Label sfxPctLabel = new Label(Math.round(SoundManager.getInstance().getSfxVolume() * 100) + "%");
        sfxPctLabel.setStyle("-fx-text-fill: #A0C4FF; -fx-font-size: 14; -fx-font-weight: bold;"
                + "-fx-min-width: 40;");

        HBox sfxRow = new HBox(20, sfxLabel, sfxVolumeSlider, sfxPctLabel);
        sfxRow.setAlignment(Pos.CENTER);
        sfxRow.setPadding(new Insets(16));
        sfxRow.setMaxWidth(500);
        sfxRow.setStyle("-fx-background-color: rgba(255,255,255,0.07);"
                + "-fx-background-radius: 12;");

        //bmg slider
        // BGM volume row
        Label bgmVolLabel = new Label("BGM Volume");
        bgmVolLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");

        bgmVolumeSlider = new Slider(0.0, 1.0, SoundManager.getInstance().getBgVolume());
        bgmVolumeSlider.setPrefWidth(240);
        bgmVolumeSlider.setStyle(
                "-fx-control-inner-background: #1A3A6B;"
                        + "-fx-accent: #27AE60;"
        );

        Label bgmPctLabel = new Label(Math.round(SoundManager.getInstance().getBgVolume() * 100) + "%");
        bgmPctLabel.setStyle("-fx-text-fill: #A0C4FF; -fx-font-size: 14; -fx-font-weight: bold;"
                + "-fx-min-width: 40;");

        HBox bgmVolRow = new HBox(20, bgmVolLabel, bgmVolumeSlider, bgmPctLabel);
        bgmVolRow.setAlignment(Pos.CENTER);
        bgmVolRow.setPadding(new Insets(16));
        bgmVolRow.setMaxWidth(500);
        bgmVolRow.setStyle("-fx-background-color: rgba(255,255,255,0.07);"
                + "-fx-background-radius: 12;");

        // Fullscreen hint row
        Label fullscreenHint = new Label("Press F11 to toggle fullscreen");
        fullscreenHint.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 13;");

        // Instructions
        Label instrTitle = new Label("How to Play");
        instrTitle.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 20; -fx-font-weight: bold;");

        Label instructions = new Label(
            "• Click two cards to flip them.\n"
          + "• If they match, they stay face-up and you earn points.\n"
          + "• If they don't match, they flip back — combo resets.\n"
          + "• Consecutive matches build a combo multiplier (×2, ×3 …).\n"
          + "• Use power-ups to gain an edge — each works once per level.\n"
          + "  👁 Reveal: shows all cards for 2 seconds.\n"
          + "  ❄ Freeze: pauses the timer for 10 seconds.\n"
          + "  💡 Hint: highlights one unmatched pair for 2 seconds.\n"
          + "• Complete all pairs before the timer runs out to advance."
        );
        instructions.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14;");
        instructions.setWrapText(true);
        instructions.setMaxWidth(560);
        instructions.setPadding(new Insets(14));
        instructions.setStyle(instructions.getStyle()
                + "-fx-background-color: rgba(255,255,255,0.05);"
                + "-fx-background-radius: 10;");

        backButton = new Button("◀  Back to Menu");
        backButton.setPrefWidth(220);
        backButton.setPrefHeight(44);
        backButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2980B9, #1A6FA8);"
          + "-fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold;"
          + "-fx-background-radius: 22; -fx-cursor: hand;"
        );

        root.getChildren().addAll(title, soundRow, bgmRow, bgmVolRow ,sfxRow,
                instrTitle, instructions, fullscreenHint, backButton);

        // No explicit width/height — see MainMenuScene for rationale.
        scene = new Scene(root);
    }

    /** {@inheritDoc} */
    @Override
    protected void bindEvents() {
        soundToggle.setOnAction(e -> {
            boolean muted = !soundToggle.isSelected();
            SoundManager.getInstance().setMuted(muted);
            soundToggle.setText(muted ? "OFF" : "ON");
            styleSoundToggle();
        });

        // Retrieve the percentage label that is the third child of sfxRow.
        // We stored sfxVolumeSlider as a field; the pct label is in the same row.
        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            float v = newVal.floatValue();
            SoundManager.getInstance().setSfxVolume(v);
            // Update the percentage label (3rd child of the slider's parent HBox)
            HBox row = (HBox) sfxVolumeSlider.getParent();
            if (row != null && row.getChildren().size() >= 3) {
                Label pct = (Label) row.getChildren().get(2);
                pct.setText(Math.round(v * 100) + "%");
            }
        });

        bgmToggle.setOnAction(e -> {
            boolean muted = !bgmToggle.isSelected();
            SoundManager.getInstance().setBgMuted(muted);
            bgmToggle.setText(muted ? "OFF" : "ON");
            styleBgmToggle();
        });

        backButton.setOnAction(e -> controller.onBack());

        bgmVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            float v = newVal.floatValue();
            SoundManager.getInstance().setBgVolume(v);
            HBox row = (HBox) bgmVolumeSlider.getParent();
            if (row != null && row.getChildren().size() >= 3) {
                Label pct = (Label) row.getChildren().get(2);
                pct.setText(Math.round(v * 100) + "%");
            }
        });
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void styleSoundToggle() {
        boolean on = soundToggle.isSelected();
        soundToggle.setStyle(
            "-fx-background-color: " + (on ? "#27AE60" : "#C0392B") + ";"
          + "-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;"
          + "-fx-background-radius: 20; -fx-pref-width: 70; -fx-pref-height: 36;"
          + "-fx-cursor: hand;"
        );
    }

    private void styleBgmToggle() {
        boolean on = bgmToggle.isSelected();
        bgmToggle.setStyle(
                "-fx-background-color: " + (on ? "#27AE60" : "#C0392B") + ";"
                        + "-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;"
                        + "-fx-background-radius: 20; -fx-pref-width: 70; -fx-pref-height: 36;"
                        + "-fx-cursor: hand;"
        );
    }
}
