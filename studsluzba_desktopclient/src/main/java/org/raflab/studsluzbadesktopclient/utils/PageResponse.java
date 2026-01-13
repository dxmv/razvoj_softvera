package org.raflab.studsluzbadesktopclient.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.List;

/**
 * Minimal representation of Spring Data REST page JSON so WebClient can
 * deserialize responses and expose the actual content list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponse<T> {

    private List<T> content = Collections.emptyList();

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content == null ? Collections.emptyList() : content;
    }
}
