package com.ekocbiyik.model.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

@Data
@MappedSuperclass
public abstract class Auditable {

    @CreationTimestamp
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private OffsetDateTime updatedDate;

}
