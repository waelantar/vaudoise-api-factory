package com.vaudoise.api_factory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("PERSON")
public class PersonEntity extends ClientEntity {

  @Column(name = "birthdate")
  private LocalDate birthdate;

  // Getters and Setters
  public LocalDate getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
  }
}
