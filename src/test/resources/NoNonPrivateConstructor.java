
import com.gnoht.tinybuilder.Builder;

@Builder
class NoNonPrivateConstructor {
  String name;

  private NoNonPrivateConstructor(String name) {
    this.name = name;
  }
}