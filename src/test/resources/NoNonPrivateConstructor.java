
import com.gnoht.biruda.Builder;

@Builder
class NoNonPrivateConstructor {
  String name;

  private NoNonPrivateConstructor(String name) {
    this.name = name;
  }
}