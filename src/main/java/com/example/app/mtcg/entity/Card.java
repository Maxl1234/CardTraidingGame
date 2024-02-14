package com.example.app.mtcg.entity;

public class Card {
    private String name;
    private String element;
    private String type;
    private String id;
    private double damage;

    public Card() {
    }

    public Card(String name, String element, String type, double damage, String id) {
        this.name = name;
        this.element = element;
        this.type = type;
        this.damage = damage;
        this.id=id;
    }
    public void setTypeAndElem(){
        if(this.name.contains("Spell")){
            this.type="Spell";
        }
        else {
            this.type="Monster";
        }
        if(this.name.contains("Water")){
            this.element="Water";
        }
        else if(this.name.contains("Fire")){
            this.element="Fire";
        }
        else{
            this.element="Normal";
        }
    }

    public void setDamage(double damage) {this.damage = damage;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public double getDamage() {
        return damage;
    }




}
