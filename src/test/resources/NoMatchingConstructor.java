
import com.gnoht.biruda.Builder;

@Builder
class NoMatchingConstructor {
  String name;

  private NoMatchingConstructor(String test) {
    this.name = test;
  }
}