package com.bahmet.model;

public class Currency {
    private Long id;
    private String code;
    private String fullName;
    private String symbol;

    public Currency() {
    }

    public Currency(Currency currency) {
        this.id = currency.id;
        this.code = currency.code;
        this.fullName = currency.fullName;
        this.symbol = currency.symbol;
    }

    public Currency(Long id, String code, String fullName, String symbol) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.symbol = symbol;
    }

    public Currency(String code, String fullName, String symbol) {
        this.code = code;
        this.fullName = fullName;
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
