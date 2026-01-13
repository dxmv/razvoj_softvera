package org.raflab.studsluzbadesktopclient.navigation;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a logical position within the client UI that can be restored later.
 * Captures the target FXML view and optional tab metadata so we can replay intra-view
 * navigation (e.g., switching tabs) via the history service.
 */
public class ViewState {

    private final String viewId;
    private final String tabPaneId;
    private final String tabId;

    private ViewState(String viewId, String tabPaneId, String tabId) {
        this.viewId = Objects.requireNonNull(viewId, "viewId must not be null");
        this.tabPaneId = tabPaneId;
        this.tabId = tabId;
    }

    public static ViewState of(String viewId) {
        return new ViewState(viewId, null, null);
    }

    public static ViewState of(String viewId, String tabPaneId, String tabId) {
        return new ViewState(viewId, tabPaneId, tabId);
    }

    public ViewState withTab(String tabPaneId, String tabId) {
        return new ViewState(this.viewId, tabPaneId, tabId);
    }

    public String getViewId() {
        return viewId;
    }

    public Optional<String> getTabPaneId() {
        return Optional.ofNullable(tabPaneId);
    }

    public Optional<String> getTabId() {
        return Optional.ofNullable(tabId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewState viewState = (ViewState) o;
        return viewId.equals(viewState.viewId)
                && Objects.equals(tabPaneId, viewState.tabPaneId)
                && Objects.equals(tabId, viewState.tabId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(viewId, tabPaneId, tabId);
    }

    @Override
    public String toString() {
        return "ViewState{" +
                "viewId='" + viewId + '\'' +
                (tabPaneId != null ? ", tabPaneId='" + tabPaneId + '\'' : "") +
                (tabId != null ? ", tabId='" + tabId + '\'' : "") +
                '}';
    }
}
