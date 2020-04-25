# Tiny Builder
A little tool for auto generating builders. 

Given a target class
```java
class Person {
  String name;
  Integer age;
  
  Person(String name, Integer age) {
    this.name = name;
    this.age = age;
  }
  
  public String getName() {
    return name;
  }
  
  public Integer getAge() {
    return age;
  }
}
```
A `PersonBuilder` will be auto generated to the same package as `Person`, when `@Builder` is applied in any of the following ways.

At the type level for the target class.
```java
@Builder
class Person {...}
```
On any non-private, non-empty constructor in the target class.
```java
class Person {
  ...
  @Builder
  Person(String name, Integer) {...}
}
```
Finally on any non-private, non-empty, accessible static factory method. 
```java
class Factory {
  @Builder
  static Person createPerson(String name, Integer age) {...}
}
```
The resulting `PersonBuilder` is auto generated.
```java
import java.lang.Integer;
import java.lang.String;

/**
 * DO NOT EDIT, AUTO-GENERATED CODE
 */
public class PersonBuilder {
  private String name;

  private Integer age;

  public static PersonBuilder builder() {
    return new PersonBuilder();
  }

  public PersonBuilder name(String name) {
    this.name = name;
    return this;
  }

  public PersonBuilder nameIfPresent(String name) {
    if (name != null) {
      this.name = name;
    }
    return this;
  }

  public PersonBuilder age(Integer age) {
    this.age = age;
    return this;
  }

  public PersonBuilder ageIfPresent(Integer age) {
    if (age != null) {
      this.age = age;
    }
    return this;
  }

  public static PersonBuilder with(Person person) {
    return new PersonBuilder()
        .name(person.getName())
        .age(person.getAge());
  }

  public Person build() {
    return new Person(name,age);
  }
}
```

