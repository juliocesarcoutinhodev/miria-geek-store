package br.com.miriageekstore.identity.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Address {

    private final UUID id;
    private final UserId userId;
    private String alias;
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private boolean isDefault;
    private final Instant createdAt;

    private Address(UUID id, UserId userId, String alias, String zipCode, String street,
                    String number, String complement, String neighborhood, String city,
                    String state, boolean isDefault, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.alias = alias;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public static Address create(UserId userId, String alias, String zipCode, String street,
                                  String number, String complement, String neighborhood,
                                  String city, String state, boolean isDefault) {
        return new Address(UUID.randomUUID(), userId, alias, zipCode, street, number,
                complement, neighborhood, city, state, isDefault, Instant.now());
    }

    public static Address reconstitute(UUID id, UserId userId, String alias, String zipCode,
                                        String street, String number, String complement,
                                        String neighborhood, String city, String state,
                                        boolean isDefault, Instant createdAt) {
        return new Address(id, userId, alias, zipCode, street, number, complement,
                neighborhood, city, state, isDefault, createdAt);
    }

    public void update(String alias, String zipCode, String street, String number,
                       String complement, String neighborhood, String city, String state) {
        this.alias = alias;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
    }

    public void markAsDefault() { this.isDefault = true; }
    public void clearDefault()  { this.isDefault = false; }

    public UUID getId()            { return id; }
    public UserId getUserId()      { return userId; }
    public String getAlias()       { return alias; }
    public String getZipCode()     { return zipCode; }
    public String getStreet()      { return street; }
    public String getNumber()      { return number; }
    public String getComplement()  { return complement; }
    public String getNeighborhood(){ return neighborhood; }
    public String getCity()        { return city; }
    public String getState()       { return state; }
    public boolean isDefault()     { return isDefault; }
    public Instant getCreatedAt()  { return createdAt; }
}
