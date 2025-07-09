package com.hibernate.domain;

public enum Rating {
    G("G"), PG("PG"), PG13("PG-13"), R("R"), NR17("NC-17");

    private final String value;

    Rating(String value) {
        this.value = value;
    }
}
