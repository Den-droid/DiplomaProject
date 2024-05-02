package org.example.apiapplication.helpers;

public class ScholarQueryBuilder {
    private final String BASE_URL = "https://scholar.google.com.ua";

    private String url = BASE_URL;

    public static ScholarQueryBuilder builder() {
        return new ScholarQueryBuilder();
    }

    public ScholarQueryBuilder build() {
        return this;
    }

    public ScholarQueryBuilder citations() {
        this.url += "/citations";
        return this;
    }

    public ScholarQueryBuilder startParameters() {
        this.url += "?";
        return this;
    }

    public ScholarQueryBuilder hl(String hl) {
        appendDelimiter();

        this.url += "hl=" + hl;
        return this;
    }

    public ScholarQueryBuilder user(String userId) {
        appendDelimiter();

        this.url += "user=" + userId;
        return this;
    }

    public ScholarQueryBuilder view_op(String viewOp) {
        appendDelimiter();

        this.url += "view_op=" + viewOp;
        return this;
    }

    public ScholarQueryBuilder sortBy(String sortBy) {
        appendDelimiter();

        this.url += "sortby=" + sortBy;
        return this;
    }

    private void appendDelimiter() {
        if (this.url.lastIndexOf("?") != this.url.length() - 1) {
            this.url += "&";
        }
    }

    @Override
    public String toString() {
        return this.url;
    }
}
