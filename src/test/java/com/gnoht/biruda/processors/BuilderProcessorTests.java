package com.gnoht.biruda.processors;

import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

/**
 * @author ikumen@gnoht.com
 */
public class BuilderProcessorTests {

  @Test
  public void shouldGenerateBuilder() {
    Compilation compilation = javac().withProcessors(new BuilderProcessor())
        .compile(JavaFileObjects.forResource("BuilderFromConstructor.java"));
    assertThat(compilation).succeeded();
    assertThat(compilation)
        .generatedSourceFile("BuilderFromConstructorBuilder")
        .hasSourceEquivalentTo(JavaFileObjects.forResource("BuilderFromConstructorBuilder.java"));
  }

  @Test
  public void shouldFailWhenMissingGetter() {
    Compilation compilation = javac().withProcessors(new BuilderProcessor())
        .compile(JavaFileObjects.forResource("MissingGetter.java"));
    assertThat(compilation).failed();
    assertThat(compilation).hadErrorContaining("'with' static factory method requires getters for every");
  }

  @Test
  public void shouldFailWhenNoMatchConstructors() {
    Compilation compilation = javac().withProcessors(new BuilderProcessor())
        .compile(JavaFileObjects.forResource("NoMatchingConstructor.java"));
    assertThat(compilation).failed();
    assertThat(compilation).hadErrorContaining("Unable to resolve a constructor for the target class: NoMatchingConstructor");
  }
}
