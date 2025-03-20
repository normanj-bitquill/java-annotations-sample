package com.improving.vancouver.annotations.endpoints;

import com.improving.vancouver.annotations.annotations.Argument;
import com.improving.vancouver.annotations.annotations.DeleteMethod;
import com.improving.vancouver.annotations.annotations.GetMethod;
import com.improving.vancouver.annotations.annotations.PostMethod;
import com.improving.vancouver.annotations.annotations.RestEndpoint;
import com.improving.vancouver.annotations.model.Person;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestEndpoint("/person")
public class PersonEndpoint {
  private final List<Person> people = new ArrayList<>();
  private final AtomicInteger idGenerator = new AtomicInteger(1);

  @GetMethod
  @Argument(name = "id", description = "ID of person")
  public Person getPerson(int id) {
    for (Person person : people) {
      if (person.getId() == id) {
        return person;
      }
    }

    return null;
  }

  @PostMethod(Person.class)
  public void postPerson(Person person) {
    if (person.getId() != -1) {
      for (Person existingPerson : people) {
        if (existingPerson.getId() == person.getId()) {
          existingPerson.setName(person.getName());
          existingPerson.setAge(person.getAge());
          break;
        }
      }
    } else {
      people.add(new Person(idGenerator.getAndIncrement(), person.getName(), person.getAge()));
    }
  }

  @PostMethod(Person.class)
  public void putPerson(Person person) {
    if (person.getId() != -1) {
      for (Person existingPerson : people) {
        if (existingPerson.getId() == person.getId()) {
          existingPerson.setName(person.getName());
          existingPerson.setAge(person.getAge());
          break;
        }
      }
    }
  }

  @DeleteMethod
  @Argument(name = "id", description = "ID of person")
  @Argument(name = "confirm", description = "Confirm delete (y/n)")
  public void deletePerson(int id) {
    for (Iterator<Person> it = people.iterator(); it.hasNext(); ) {
      Person person = it.next();
      if (person.getId() == id) {
        it.remove();
        break;
      }
    }
  }
}
