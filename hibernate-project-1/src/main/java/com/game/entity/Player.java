package com.game.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "player", schema = "rpg")
@NamedQuery(name = "player_getAllCount", query = "select count(p) from Player p")
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(length = 12, nullable = false)
  private String name;

  @Column(length = 30, nullable = false)
  private String title;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "race", length = 30)
  private Race race;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "profession", length = 30)
  private Profession profession;

  @Column(name = "birthday", nullable = false)
  private Date birthday;

  @Column(name = "banned", nullable = false)
  private Boolean banned;

  @Column(name = "level", nullable = false)
  private Integer level;

  public Player() {}

  public Player(
      Long id,
      String name,
      String title,
      Race race,
      Profession profession,
      Date birthday,
      Boolean banned,
      Integer level) {
    this.id = id;
    this.name = name;
    this.title = title;
    this.race = race;
    this.profession = profession;
    this.birthday = birthday;
    this.banned = banned;
    this.level = level;
  }

  // Getters

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public Race getRace() {
    return race;
  }

  public Profession getProfession() {
    return profession;
  }

  public Date getBirthday() {
    return birthday;
  }

  public Boolean getBanned() {
    return banned;
  }

  public Integer getLevel() {
    return level;
  }

  // Setters

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setRace(Race race) {
    this.race = race;
  }

  public void setProfession(Profession profession) {
    this.profession = profession;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public void setBanned(Boolean banned) {
    this.banned = banned;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }
}
