
import com.gnoht.tinybuilder.Builder;

@Builder
class BuilderFromConstructor {
  String name;
  Integer count;

  BuilderFromConstructor(String name, Integer count) {
    this.name = name;
    this.count = count;
  }

  public String getName() {
    return name;
  }

  public Integer getCount() {
    return count;
  }
}