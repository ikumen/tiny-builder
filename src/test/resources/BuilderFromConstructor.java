
import com.gnoht.tinybuilder.Builder;

@Builder
class BuilderFromConstructor {
  String name;
  int count;
  boolean shared;

  public BuilderFromConstructor(String name, int count, boolean shared) {
    this.name = name;
    this.count = count;
    this.shared = shared;
  }

  public String getName() {
    return name;
  }

  public int getCount() {
    return count;
  }

  public boolean isShared() {
    return shared;
  }
}