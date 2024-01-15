package at.jku.se.prse.team3;

import java.util.UUID;

public class Kategorie {
    private final UUID id;
    private String name;

    public Kategorie(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }
}
