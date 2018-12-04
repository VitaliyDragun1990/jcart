package com.revenat.jcart.core.base;

import com.revenat.jcart.core.converters.InstantConverter;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    protected Integer id;

    @Convert(converter = InstantConverter.class)
    @Column(name = "created_on", nullable = false)
    private Instant createdOn;

    @Convert(converter = InstantConverter.class)
    @Column(name = "modified_on", nullable = false)
    private Instant modifiedOn;

    public AbstractEntity() {

        this.createdOn = Instant.now();
        this.modifiedOn = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public Instant getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Instant modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "";
    }
}
