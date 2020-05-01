
import com.gnoht.tinybuilder.Builder;

@Builder
class BuildTargetWithMultipleConstructors {
  String name;
  int count;
  boolean shared;
  long key;
  int age;
  String address;

  public BuildTargetWithMultipleConstructors(String name, int count, String address, long key) {
    this.name = name;
    this.count = count;
    this.address = address;
    this.key = key;
  }

  public BuildTargetWithMultipleConstructors(String name, int count, boolean shared, int age) {
    this.name = name;
    this.count = count;
    this.shared = shared;
    this.age = age;
  }

  // this should be the selected
  public BuildTargetWithMultipleConstructors(String name, int count, boolean shared, int age, long key) {
    this.name = name;
    this.count = count;
    this.shared = shared;
    this.age = age;
    this.key = key;
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

  public int getAge() {
    return age;
  }

  public long getKey() {
    return key;
  }

  public String getAddress() {
    return address;
  }
}