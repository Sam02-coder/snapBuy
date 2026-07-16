package com.snapBuy.category.entity;

import com.snapBuy.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String description;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}