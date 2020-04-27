
import com.gnoht.tinybuilder.Builder;

@Builder
class MissingGetter {
  String name;
  Integer count;

  private MissingGetter(String name, Integer count) {
    this.name = name;
    this.count = count;
  }
}