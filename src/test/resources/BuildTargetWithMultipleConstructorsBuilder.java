import java.lang.String;

/**
 * DO NOT EDIT, AUTO-GENERATED CODE
 */
public class BuildTargetWithMultipleConstructorsBuilder {
  private String name;

  private int count;

  private boolean shared;

  private int age;

  private long key;

  public static BuildTargetWithMultipleConstructorsBuilder builder() {
    return new BuildTargetWithMultipleConstructorsBuilder();
  }

  public BuildTargetWithMultipleConstructorsBuilder name(String name) {
    this.name = name;
    return this;
  }

  public BuildTargetWithMultipleConstructorsBuilder count(int count) {
    this.count = count;
    return this;
  }

  public BuildTargetWithMultipleConstructorsBuilder shared(boolean shared) {
    this.shared = shared;
    return this;
  }

  public BuildTargetWithMultipleConstructorsBuilder age(int age) {
    this.age = age;
    return this;
  }

  public BuildTargetWithMultipleConstructorsBuilder key(long key) {
    this.key = key;
    return this;
  }

  public static BuildTargetWithMultipleConstructorsBuilder with(
      BuildTargetWithMultipleConstructors buildTargetWithMultipleConstructors) {
    return new BuildTargetWithMultipleConstructorsBuilder()
        .name(buildTargetWithMultipleConstructors.getName())
        .count(buildTargetWithMultipleConstructors.getCount())
        .shared(buildTargetWithMultipleConstructors.isShared())
        .age(buildTargetWithMultipleConstructors.getAge())
        .key(buildTargetWithMultipleConstructors.getKey());
  }

  public BuildTargetWithMultipleConstructors build() {
    return new BuildTargetWithMultipleConstructors(name,count,shared,age,key);
  }
}