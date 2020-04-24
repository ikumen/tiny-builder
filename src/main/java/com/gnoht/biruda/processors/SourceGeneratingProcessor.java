package com.gnoht.biruda.processors;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author ikumen@gnoht.com
 */
public abstract class SourceGeneratingProcessor extends AbstractProcessor {
  protected Elements elementUtils;
  protected Types typeUtils;
  protected Filer filer;
  protected Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
        PackageElement packageElement = getPackageElement(element);
        try {
          TypeSpec typeSpec = generate(element);
          JavaFile.builder(ProcessingHelper.getQualifiedName(packageElement), typeSpec)
              .build()
              .writeTo(filer);
        } catch (Exception e) {
          error(element, e.getMessage());
          return true;
        }
      }
    }
    return true;
  }

  protected abstract TypeSpec generate(Element annotatedElement);

  protected PackageElement getPackageElement(Element annotatedElement) {
    return elementUtils.getPackageOf(annotatedElement);
  }

  protected void error(Element element, String msg, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
