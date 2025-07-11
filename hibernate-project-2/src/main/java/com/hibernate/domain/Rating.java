package com.hibernate.domain;

import lombok.Data;
import lombok.Getter;

@Getter
public enum Rating {
    G("G"), PG("PG"), PG13("PG-13"), R("R"), NR17("NC-17");

    private final String value;

    Rating(String value) {
        this.value = value;
    }
}
