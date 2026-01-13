package org.raflab.studsluzbadesktopclient.navigation;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Keeps track of view navigation so users can go back/forward just like in a browser.
 */
@Service
public class NavigationService {

    private final Deque<ViewState> backStack = new ArrayDeque<>();
    private final Deque<ViewState> forwardStack = new ArrayDeque<>();
    private final int maxDepth;

    private ViewState currentState;
    private Consumer<ViewState> renderer;

    public NavigationService(@Value("${client.history.maxDepth:10}") int maxDepth) {
        this.maxDepth = Math.max(1, maxDepth);
    }

    public void registerRenderer(Consumer<ViewState> renderer) {
        this.renderer = Objects.requireNonNull(renderer);
    }

    public void initialize(ViewState initialState) {
        if (currentState != null) {
            return;
        }
        currentState = Objects.requireNonNull(initialState);
        render(currentState);
    }

    public void visit(ViewState newState) {
        Objects.requireNonNull(newState, "newState must not be null");
        if (currentState != null) {
            backStack.push(currentState);
            trimBackStack();
        }
        currentState = newState;
        forwardStack.clear();
        render(newState);
    }

    public boolean back() {
        if (backStack.isEmpty() || currentState == null) {
            return false;
        }
        forwardStack.push(currentState);
        currentState = backStack.pop();
        render(currentState);
        return true;
    }

    public boolean forward() {
        if (forwardStack.isEmpty() || currentState == null) {
            return false;
        }
        backStack.push(currentState);
        currentState = forwardStack.pop();
        render(currentState);
        return true;
    }

    public ViewState getCurrentState() {
        return currentState;
    }

    private void trimBackStack() {
        while (backStack.size() > maxDepth) {
            backStack.removeLast();
        }
    }

    private void render(ViewState state) {
        if (renderer != null) {
            renderer.accept(state);
        }
    }
}
