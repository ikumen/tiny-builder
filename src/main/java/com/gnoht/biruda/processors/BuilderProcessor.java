package com.gnoht.biruda.processors;

import static com.gnoht.biruda.processors.ProcessingHelper.*;

import com.gnoht.biruda.Builder;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.Processor;
import javax.lang.model.element.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ikumen@gnoht.com
 */
@AutoService(Processor.class)
public class BuilderProcessor extends SourceGeneratingProcessor {

  @Override
  protected TypeSpec generate(Element targetElement) {
    ElementKind kind = targetElement.getKind();
    Set<Modifier> modifiers = targetElement.getModifiers();

    // We support annotations on concrete classes ..
    if (kind.equals(ElementKind.CLASS) &&
        !modifiers.contains(Modifier.ABSTRACT)) {
      return generateFor((TypeElement) targetElement);
    // and non-private, non-empty constructors ...
    } else if (kind.equals(ElementKind.CONSTRUCTOR) && !modifiers.contains(Modifier.PRIVATE) &&
        ((ExecutableElement) targetElement).getParameters().size() > 0) {
      return generateFor((ExecutableElement) targetElement, true);
    // and accessible, static, non-private, non-empty factory methods
    } else if (kind.equals(ElementKind.METHOD) && modifiers.contains(Modifier.STATIC) &&
        (modifiers.contains(Modifier.PUBLIC) || (!modifiers.contains(Modifier.PRIVATE) &&
            typeUtils.asElement(((ExecutableElement) targetElement).getReturnType())
                .equals(targetElement.getEnclosingElement())))) {
      return generateFor((ExecutableElement) targetElement, false);
    }

    throw new ProcessingException(targetElement,
        "Builder can only target concrete classes, call non-private constructors/static " +
        "methods, and package/protected static methods of target classes");

  }

  /* Helper for determining if given constructor parameter corresponds class instance field */
  private boolean isParameterInTargetFields(VariableElement parameter, Set<VariableElement> fields) {
    TypeName paramTypeName = getTypeName(parameter);
    String paramName = getSimpleName(parameter);
    for (VariableElement field: fields) {
      if (paramTypeName.equals(getTypeName(field))
          && paramName.equals(getSimpleName(field)))
        return true;
    }
    return false;
  }

  /*
   * When a target class that we are generating a builder for has multiple
   * constructors, try to find the first constructor that matches all
   * the instance fields in the target class, and use that as a target constructor.
   */
  private ExecutableElement resolveTargetConstructor(TypeElement targetType) {
    Set<VariableElement> fields = new HashSet<>();
    Set<ExecutableElement> constructors = new HashSet<>();
    for (Element member: targetType.getEnclosedElements()) {
      if (member.getKind() == ElementKind.FIELD
          && member.getAnnotation(Builder.Ignored.class) == null
          && !member.getModifiers().contains(Modifier.STATIC)) {
        fields.add((VariableElement) member);
      } else if (member.getKind() == ElementKind.CONSTRUCTOR) {
        constructors.add((ExecutableElement) member);
      }
    }
    // Return the first constructor that matches all the fields in our target
    for (ExecutableElement constructor: constructors) {
      if (fields.size() == constructor.getParameters().size()
          && fields.size() == constructor.getParameters().stream()
              .filter(param -> isParameterInTargetFields(param, fields)).count()) {
        return constructor;
      }
    }

    throw new ProcessingException(targetType,
        "Unable to resolve a constructor for the target class: " + getSimpleName(targetType));
  }

  /* Create the initial class TypeSpec Builder */
  private TypeSpec.Builder getClassBuilder(final ClassName builderClassName, List<? extends VariableElement> parameters) {
    final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(builderClassName)
        .addJavadoc("DO NOT EDIT, AUTO-GENERATED CODE")
        .addModifiers(Modifier.PUBLIC);

    parameters.forEach(param -> {
      TypeName paramTypeName = getTypeName(param);
      String paramName = getSimpleName(param);

      classBuilder
        .addField(paramTypeName, paramName, Modifier.PRIVATE)
        .addMethod(
            MethodSpec.methodBuilder(paramName)
              .addModifiers(Modifier.PUBLIC)
              .addParameter(paramTypeName, paramName)
              .addStatement("this.$1L = $1L", paramName)
              .addStatement("return this")
              .returns(builderClassName)
              .build());

      if (!paramTypeName.isPrimitive()) {
        classBuilder.addMethod(
          MethodSpec.methodBuilder(paramName + "IfPresent")
              .addModifiers(Modifier.PUBLIC)
              .addParameter(paramTypeName, paramName)
              .addCode(CodeBlock.builder()
                  .beginControlFlow("if ($1L != null)", paramName)
                  .addStatement("this.$1L = $1L", paramName)
                  .endControlFlow()
                  .build())
              .addStatement("return this")
              .returns(builderClassName)
              .build());
      }
    });
    return classBuilder;
  }

  /*
   * Generate the builder for a class that was annotated with @Builder. First
   * try to find a suitable constructor. We generate a builder based on the
   * parameters of the constructor.
   */
  private TypeSpec generateFor(TypeElement targetType) {
    ClassName builderClassName = getBuilderClassName(targetType, targetType);
    ExecutableElement targetConstructor = resolveTargetConstructor(targetType);
    List<? extends VariableElement> targetParameters = targetConstructor.getParameters();

    // Generate build class declaration
    TypeSpec.Builder classBuilder = getClassBuilder(builderClassName, targetParameters)
        .addMethod(getOfMethod(builderClassName))
        .addMethod(
            MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return new $T($L)", targetType,
                    targetParameters.stream().map(p -> getSimpleName(p))
                    .collect(Collectors.joining(",")))
                .returns(getTypeName(targetType))
                .build());

    return classBuilder.build();
  }

  /*
   * Generate the builder for a constructor or static factory method that was
   * annotated with @Builder. We generate a builder based on the parameters of
   * the constructor/factory method.
   */
  private TypeSpec generateFor(ExecutableElement targetExecutable, boolean isConstructor) {
    TypeElement enclosingType = (TypeElement) targetExecutable.getEnclosingElement();
    TypeElement targetType = isConstructor ? enclosingType : (TypeElement) typeUtils.asElement(targetExecutable.getReturnType());
    ClassName builderClassName = getBuilderClassName(targetType, targetExecutable);
    List<? extends VariableElement> targetParameters = targetExecutable.getParameters();

    MethodSpec.Builder buildMethodBuilder = MethodSpec
        .methodBuilder("build")
        .addModifiers(Modifier.PUBLIC)
        .returns(getTypeName(targetType));

    if (isConstructor) {
      buildMethodBuilder.addStatement("return new $T($L)", targetType,
          targetParameters.stream().map(p -> getSimpleName(p))
              .collect(Collectors.joining(",")));
    } else {
      buildMethodBuilder.addStatement("return $T.$L($L)", enclosingType, getSimpleName(targetExecutable),
          targetParameters.stream().map(p -> getSimpleName(p))
              .collect(Collectors.joining(",")));
    }

    return getClassBuilder(builderClassName, targetParameters)
        .addMethod(buildMethodBuilder.build())
        .addMethod(getOfMethod(builderClassName))
        .build();
  }

  /*
   * Helper for generating the static "of" factory method, that constructs
   * our builder class.
   */
  private MethodSpec getOfMethod(ClassName builderClassName) {
    return MethodSpec.methodBuilder("of")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addStatement("return new $T()", builderClassName)
        .returns(builderClassName)
        .build();
  }

  /* Returns the generated Builder Class name depending on the given Element */
  private ClassName getBuilderClassName(TypeElement targetElement, Element annotated) {
    Builder builder = annotated.getAnnotation(Builder.class);
    return getClassName(elementUtils.getPackageOf(targetElement),
        builder.prefix() + getSimpleName(targetElement) + builder.suffix());
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return new HashSet<>(Arrays.asList(Builder.class.getName()));
  }
}
