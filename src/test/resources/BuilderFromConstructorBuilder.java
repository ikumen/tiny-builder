import java.lang.String;

/**
 * DO NOT EDIT, AUTO-GENERATED CODE
 */
public class BuilderFromConstructorBuilder {
  private String name;

  private int count;

  private boolean shared;

  public static BuilderFromConstructorBuilder builder() {
    return new BuilderFromConstructorBuilder();
  }

  public BuilderFromConstructorBuilder name(String name) {
    this.name = name;
    return this;
  }

  public BuilderFromConstructorBuilder count(int count) {
    this.count = count;
    return this;
  }

  public BuilderFromConstructorBuilder shared(boolean shared) {
    this.shared = shared;
    return this;
  }

  public static BuilderFromConstructorBuilder with(BuilderFromConstructor builderFromConstructor) {
    return new BuilderFromConstructorBuilder()
            .name(builderFromConstructor.getName())
            .count(builderFromConstructor.getCount())
            .shared(builderFromConstructor.isShared());
  }

  public BuilderFromConstructor build() {
    return new BuilderFromConstructor(name,count,shared);
  }
}