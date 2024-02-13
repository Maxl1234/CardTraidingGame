package com.example.app.mtcg.entity;

import java.util.UUID;

public class Card {
    private String name;
    private String element;
    private String type;
    private UUID id;
    private float damage;


    public Card(String name, String element, String type, float damage, UUID id) {
        this.name = name;
        this.element = element;
        this.type = type;
        this.damage = damage;
        this.id=id;
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public float getDamage() {
        return damage;
    }




}
