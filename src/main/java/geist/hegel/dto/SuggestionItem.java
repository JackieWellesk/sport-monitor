package geist.hegel.dto;

import lombok.Data;

@Data
public class SuggestionItem {
    private SuggestionType type;
    private String title;
    private String detail;

    public SuggestionItem() {}
    public SuggestionItem(SuggestionType type, String title, String detail) {
        this.type = type;
        this.title = title;
        this.detail = detail;
    }
    // getter/setter
}