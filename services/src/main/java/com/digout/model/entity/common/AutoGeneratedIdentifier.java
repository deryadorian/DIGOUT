/**
 *
 */
package com.digout.model.entity.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.digout.utils.Serializations;

@MappedSuperclass
public abstract class AutoGeneratedIdentifier implements Serializable {
    private static final long serialVersionUID = -5061470460996203198L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == AutoGeneratedIdentifier.class) {
            AutoGeneratedIdentifier other = (AutoGeneratedIdentifier) o;
            return other.getId() != null && other.getId().equals(this.id);
        }
        return false;
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        int hash = 37;
        return hash * 17 * (this.id != null ? hash * 17 + this.id.hashCode() : 1);
    }

    public void setId(final Long id) {
        this.id = id;
    }
    
    public <T extends AutoGeneratedIdentifier> T cloneEntity() {
        @SuppressWarnings("unchecked")
        final T clone = Serializations.clone((T)this);
        clone.setId(null);
        return clone;
    }
}
