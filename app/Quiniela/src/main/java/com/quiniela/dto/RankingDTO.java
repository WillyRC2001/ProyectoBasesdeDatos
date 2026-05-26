package com.quiniela.dto;

public class RankingDTO {

    private int posicion;
    private String nombreUsuario;
    private int puntosTotal;

    public RankingDTO(int posicion, String nombreUsuario, int puntosTotal) {
        this.posicion = posicion;
        this.nombreUsuario = nombreUsuario;
        this.puntosTotal = puntosTotal;
    }

    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { this.posicion = posicion; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public int getPuntosTotal() { return puntosTotal; }
    public void setPuntosTotal(int puntosTotal) { this.puntosTotal = puntosTotal; }
}