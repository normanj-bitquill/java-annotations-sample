package com.improving.vancouver.annotations.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Person {
  private int id;
  private String name;
  private int age;

  public Person(String name, int age) {
    this.id = -1;
    this.name = name;
    this.age = age;
  }

  public Person(int id, String name, int age) {
    this.id = id;
    this.name = name;
    this.age = age;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }
}
