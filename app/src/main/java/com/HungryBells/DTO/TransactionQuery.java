package com.HungryBells.DTO;

import lombok.Data;

@Data
public class TransactionQuery {
    private String key;
    private Object value;


    public TransactionQuery(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
