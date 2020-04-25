
import com.gnoht.biruda.Builder;

@Builder
class MissingGetter {
  String name;
  Integer count;

  private MissingGetter(String name, Integer count) {
    this.name = name;
    this.count = count;
  }
}