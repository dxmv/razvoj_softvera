package org.raflab.studsluzbadesktopclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.raflab.studsluzbadesktopclient.controllers.MainWindowController;
import org.raflab.studsluzbadesktopclient.navigation.NavigationService;
import org.raflab.studsluzbadesktopclient.navigation.ViewState;
import org.springframework.stereotype.Component;

@Component
public class MainView {

    private static final String DEFAULT_VIEW = "searchStudent";

    private static final long TOUCH_GESTURE_COOLDOWN_NANOS = 350_000_000L; // ~350ms
    private static final double MIN_HORIZONTAL_SCROLL_DELTA = 35;

    private final ContextFXMLLoader appFXMLLoader;
    private final NavigationService navigationService;

    private Scene scene;
    private MainWindowController mainWindowController;
    private Parent activeRoot;
    private String activeViewId;
    private final Map<TabPane, ChangeListener<Tab>> tabListeners = new HashMap<>();
    private boolean suppressNavigationEvents;
    private long lastGestureNanos;

    public MainView(ContextFXMLLoader appFXMLLoader, NavigationService navigationService) {
        this.appFXMLLoader = appFXMLLoader;
        this.navigationService = navigationService;
    }

    public Scene createScene() {
        try {
            FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/main.fxml"));
            BorderPane borderPane = loader.load();
            this.mainWindowController = loader.getController();
            this.scene = new Scene(borderPane, 1000, 800);
            scene.getStylesheets().add(Objects.requireNonNull(MainView.class.getResource("/css/stylesheet.css")).toExternalForm());
            navigationService.registerRenderer(this::renderView);
            navigationService.initialize(ViewState.of(DEFAULT_VIEW));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.scene;
    }

    public void bindNavigationInputs(Scene targetScene) {
        if (targetScene == null) {
            return;
        }
        targetScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.BACK) {
                if (navigationService.back()) {
                    event.consume();
                }
            } else if (event.getButton() == MouseButton.FORWARD) {
                if (navigationService.forward()) {
                    event.consume();
                }
            }
        });

        targetScene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.OPEN_BRACKET, KeyCombination.CONTROL_DOWN),
                navigationService::back);
        targetScene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.CLOSE_BRACKET, KeyCombination.CONTROL_DOWN),
                navigationService::forward);

        targetScene.addEventFilter(SwipeEvent.ANY, event -> {
            boolean requestBack = event.getEventType() == SwipeEvent.SWIPE_LEFT;
            boolean requestForward = event.getEventType() == SwipeEvent.SWIPE_RIGHT;
            if (!requestBack && !requestForward) {
                return;
            }
            if (!allowGestureTrigger()) {
                event.consume();
                return;
            }
            boolean handled = requestBack ? navigationService.back() : navigationService.forward();
            if (handled) {
                event.consume();
            }
        });

        targetScene.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (!event.isDirect()) {
                return;
            }
            double absX = Math.abs(event.getDeltaX());
            if (absX < MIN_HORIZONTAL_SCROLL_DELTA || absX < Math.abs(event.getDeltaY())) {
                return;
            }
            if (!allowGestureTrigger()) {
                event.consume();
                return;
            }
            boolean handled = event.getDeltaX() > 0 ? navigationService.back() : navigationService.forward();
            if (handled) {
                event.consume();
            }
        });
    }

    public void navigateTo(String fxml) {
        navigationService.visit(ViewState.of(fxml));
    }

    private boolean allowGestureTrigger() {
        long now = System.nanoTime();
        if (now - lastGestureNanos < TOUCH_GESTURE_COOLDOWN_NANOS) {
            return false;
        }
        lastGestureNanos = now;
        return true;
    }

    public void openModal(String fxml) {
        openModal(fxml, null, 400, 300);
    }

    public void openModal(String fxml, String title) {
        openModal(fxml, title, 400, 300);
    }

    public void openModal(String fxml, String title, int width, int height) {
        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + fxml + ".fxml"));
        try {
            Parent parent = loader.load();
            Scene modalScene = new Scene(parent, width, height);
            Stage stage = new Stage();
            if (title != null) {
                stage.setTitle(title);
            }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(modalScene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderView(ViewState state) {
        if (mainWindowController == null) {
            return;
        }

        if (Objects.equals(activeViewId, state.getViewId()) && activeRoot != null) {
            applyStateToActiveView(state);
            return;
        }

        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + state.getViewId() + ".fxml"));
        try {
            Parent root = loader.load();
            activeRoot = root;
            activeViewId = state.getViewId();
            mainWindowController.setContent(root);
            registerTabNavigationHandlers(state.getViewId(), root);
            applyStateToActiveView(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerTabNavigationHandlers(String viewId, Parent root) {
        unregisterTabListeners();
        if (root == null) {
            return;
        }
        for (TabPane pane : findTabPanes(root)) {
            if (pane.getId() == null) {
                continue;
            }
            ChangeListener<Tab> listener = (obs, oldTab, newTab) -> {
                if (suppressNavigationEvents || newTab == null || newTab.getId() == null) {
                    return;
                }
                navigationService.visit(ViewState.of(viewId, pane.getId(), newTab.getId()));
            };
            pane.getSelectionModel().selectedItemProperty().addListener(listener);
            tabListeners.put(pane, listener);
        }
    }

    private void unregisterTabListeners() {
        tabListeners.forEach((pane, listener) -> pane.getSelectionModel().selectedItemProperty().removeListener(listener));
        tabListeners.clear();
    }

    private List<TabPane> findTabPanes(Node root) {
        List<TabPane> panes = new ArrayList<>();
        collectTabPanes(root, panes);
        return panes;
    }

    private void collectTabPanes(Node node, List<TabPane> panes) {
        if (node instanceof TabPane pane) {
            panes.add(pane);
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectTabPanes(child, panes);
            }
        }
    }

    private void applyStateToActiveView(ViewState state) {
        if (activeRoot == null) {
            return;
        }
        if (state.getTabPaneId().isEmpty() || state.getTabId().isEmpty()) {
            return;
        }
        TabPane pane = findTabPaneById(activeRoot, state.getTabPaneId().get());
        if (pane == null) {
            return;
        }
        Tab targetTab = pane.getTabs().stream()
                .filter(tab -> state.getTabId().get().equals(tab.getId()))
                .findFirst()
                .orElse(null);
        if (targetTab == null || pane.getSelectionModel().getSelectedItem() == targetTab) {
            return;
        }
        suppressNavigationEvents = true;
        try {
            pane.getSelectionModel().select(targetTab);
        } finally {
            suppressNavigationEvents = false;
        }
    }

    private TabPane findTabPaneById(Node node, String paneId) {
        if (node instanceof TabPane pane && paneId.equals(pane.getId())) {
            return pane;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                TabPane found = findTabPaneById(child, paneId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
