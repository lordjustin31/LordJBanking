package com.ciicc.orozbank.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseEntity {

    private long id;
    private LocalDateTime createdAt;
}