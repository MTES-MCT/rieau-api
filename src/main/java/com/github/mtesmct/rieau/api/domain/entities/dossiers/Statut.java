package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

import java.time.LocalDateTime;
import java.util.Objects;

public class Statut implements ValueObject<Statut> {
    private TypeStatut type;
    private LocalDateTime dateDebut;

    public LocalDateTime dateDebut() {
        return this.dateDebut;
    }

    public TypeStatut type() {
        return this.type;
    }

    public Statut(TypeStatut type, LocalDateTime dateDebut) {
        if (type == null)
            throw new NullPointerException("Le type de statut ne peut pas être nul.");
        this.type = type;
        if (dateDebut == null)
            throw new NullPointerException("La date de début du statut ne peut pas être nulle.");
        this.dateDebut = dateDebut;
    }

    @Override
    public boolean hasSameValuesAs(Statut other) {
        return other != null && Objects.equals(this.type, other.type);
    }

    @Override
    public String toString() {
        return "Statut={ type={" + Objects.toString(this.type) + "}, date début={" + Objects.toString(this.dateDebut)
                + "} }";
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final Statut other = (Statut) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }

}