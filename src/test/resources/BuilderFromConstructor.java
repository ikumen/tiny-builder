
import com.gnoht.tinybuilder.Builder;

import java.util.List;

@Builder
class BuilderFromConstructor {
  String name;
  int count;
  boolean shared;
  List<String> tags;

  public BuilderFromConstructor(String name, int count, boolean shared, List<String> tags) {
    this.name = name;
    this.count = count;
    this.shared = shared;
    this.tags = tags;
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

  public List<String> getTags() {
    return this.tags;
  }
}
