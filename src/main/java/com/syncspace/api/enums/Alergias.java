package com.syncspace.api.enums;

public enum Alergias {
    NENHUMA("Nenhuma"),
    PENICILINA("Penicilina"),
    ASPIRINA("Aspirina"),
    AMENDOIM("Amendoim"),
    LACTOSE("Lactose"),
    OUTRAS("Outras");

    private final String descricao;

    Alergias(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
