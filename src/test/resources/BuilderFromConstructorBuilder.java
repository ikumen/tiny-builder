import java.lang.Integer;
import java.lang.String;

/**
 * DO NOT EDIT, AUTO-GENERATED CODE
 */
public class BuilderFromConstructorBuilder {
  private String name;

  private Integer count;

  public static BuilderFromConstructorBuilder builder() {
    return new BuilderFromConstructorBuilder();
  }

  public BuilderFromConstructorBuilder name(String name) {
    this.name = name;
    return this;
  }

  public BuilderFromConstructorBuilder nameIfPresent(String name) {
    if (name != null) {
      this.name = name;
    }
    return this;
  }

  public BuilderFromConstructorBuilder count(Integer count) {
    this.count = count;
    return this;
  }

  public BuilderFromConstructorBuilder countIfPresent(Integer count) {
    if (count != null) {
      this.count = count;
    }
    return this;
  }

  public static BuilderFromConstructorBuilder with(BuilderFromConstructor builderFromConstructor) {
    return new BuilderFromConstructorBuilder()
            .name(builderFromConstructor.getName())
            .count(builderFromConstructor.getCount());
  }

  public BuilderFromConstructor build() {
    return new BuilderFromConstructor(name,count);
  }
}