import java.lang.String;
import java.util.List;

/**
 * DO NOT EDIT, AUTO-GENERATED CODE
 */
public class BuilderFromConstructorBuilder {
  private String name;

  private int count;

  private boolean shared;

  private List<String> tags;

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

  public BuilderFromConstructorBuilder count(int count) {
    this.count = count;
    return this;
  }

  public BuilderFromConstructorBuilder shared(boolean shared) {
    this.shared = shared;
    return this;
  }

  public BuilderFromConstructorBuilder tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public BuilderFromConstructorBuilder tagsIfPresent(List<String> tags) {
    if (tags != null) {
      this.tags = tags;
    }
    return this;
  }

  public static BuilderFromConstructorBuilder with(BuilderFromConstructor builderFromConstructor) {
    return new BuilderFromConstructorBuilder()
            .name(builderFromConstructor.getName())
            .count(builderFromConstructor.getCount())
            .shared(builderFromConstructor.isShared())
            .tags(builderFromConstructor.getTags());
  }

  public BuilderFromConstructor build() {
    return new BuilderFromConstructor(name,count,shared,tags);
  }
}