package com.quiniela.models;

import java.io.Serializable;
import java.util.Objects;

public class PartidoQuinielaId implements Serializable {

    private Long partido;
    private Long quiniela;

    public PartidoQuinielaId() {}

    public PartidoQuinielaId(Long partido, Long quiniela) {
        this.partido = partido;
        this.quiniela = quiniela;
    }

    public Long getPartido() { return partido; }
    public void setPartido(Long partido) { this.partido = partido; }

    public Long getQuiniela() { return quiniela; }
    public void setQuiniela(Long quiniela) { this.quiniela = quiniela; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartidoQuinielaId)) return false;
        PartidoQuinielaId that = (PartidoQuinielaId) o;
        return Objects.equals(partido, that.partido) &&
               Objects.equals(quiniela, that.quiniela);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partido, quiniela);
    }
}
