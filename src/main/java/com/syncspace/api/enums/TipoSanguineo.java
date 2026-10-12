package com.syncspace.api.enums;

public enum TipoSanguineo {
    A_POSITIVO("A+"),
    A_NEGATIVO("A-"),
    B_POSITIVO("B+"),
    B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"),
    AB_NEGATIVO("AB-"),
    O_POSITIVO("O+"),
    O_NEGATIVO("O-");

    private final String valor;

    TipoSanguineo(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
