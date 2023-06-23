package com.mypill.domain.survey.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Survey {


    @Id
    private Long id;

    public void setId (Long id) {
        this.id = id;
    }

    public Long getId () {
        return id;
    }
}
