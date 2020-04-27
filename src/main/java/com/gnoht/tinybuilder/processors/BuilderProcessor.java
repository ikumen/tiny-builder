package com.gnoht.tinybuilder.processors;

import com.gnoht.tinybuilder.Builder;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.Processor;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gnoht.tinybuilder.processors.ProcessingHelper.*;

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
        "Builder can only target concrete classes, non-private constructors/static " +
            "methods, and package/protected static methods of target classes");
  }

  /*
   * Helper for determining if given method parameter corresponds
   * to a set of instance field
   */
  private boolean isParameterInTargetFields(VariableElement parameter, Set<VariableElement> fields) {
    TypeName paramTypeName = getTypeName(parameter);
    String paramName = getSimpleName(parameter);
    for (VariableElement field : fields) {
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
    for (Element member : targetType.getEnclosedElements()) {
      if (member.getKind() == ElementKind.FIELD
          && member.getAnnotation(Builder.Ignored.class) == null
          && !member.getModifiers().contains(Modifier.STATIC)) {
        fields.add((VariableElement) member);
      } else if (member.getKind() == ElementKind.CONSTRUCTOR) {
        constructors.add((ExecutableElement) member);
      }
    }
    // Return the first constructor that matches all the fields in our target
    for (ExecutableElement constructor : constructors) {
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
  private TypeSpec.Builder getClassBuilder(final ClassName builderClassName,
                                           TypeElement targetType, ExecutableElement targetExecutable, Builder builderAnnotation) {
    final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(builderClassName)
        .addJavadoc("DO NOT EDIT, AUTO-GENERATED CODE")
        .addModifiers(Modifier.PUBLIC)
        .addMethod(getBuilderMethod(builderClassName));

    targetExecutable.getParameters().forEach(param -> {
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
    });

    if (builderAnnotation.allowWith() &&
        canCreateWithInstance(targetType, targetExecutable)) {
      classBuilder.addMethod(getWithInstanceMethod(builderClassName, targetType, targetExecutable));
    }

    return classBuilder;
  }

  /*
   * Generate the builder for a class that was annotated with @Builder. First
   * try to find a suitable constructor. We generate a builder based on the
   * parameters of the constructor.
   */
  private TypeSpec generateFor(TypeElement targetType) {
    Builder targetAnnotation = targetType.getAnnotation(Builder.class);
    ClassName builderClassName = getBuilderClassName(targetType, targetAnnotation);
    ExecutableElement targetConstructor = resolveTargetConstructor(targetType);
    List<? extends VariableElement> targetParameters = targetConstructor.getParameters();

    // Generate build class declaration
    TypeSpec.Builder classBuilder = getClassBuilder(builderClassName,
        targetType, targetConstructor, targetAnnotation)
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
    Builder targetAnnotation = targetExecutable.getAnnotation(Builder.class);
    ClassName builderClassName = getBuilderClassName(targetType, targetAnnotation);
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

    TypeSpec.Builder classBuilder = getClassBuilder(builderClassName,
        targetType, targetExecutable, targetAnnotation)
        .addMethod(buildMethodBuilder.build());

    return classBuilder.build();
  }

  /*
   * Helper for generating the static "builder" factory method, that constructs
   * our builder class.
   */
  private MethodSpec getBuilderMethod(ClassName builderClassName) {
    return MethodSpec.methodBuilder("builder")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addStatement("return new $T()", builderClassName)
        .returns(builderClassName)
        .build();
  }

  private MethodSpec getWithInstanceMethod(ClassName builderClassName, TypeElement targetType, ExecutableElement targetExecutable) {
    final String unCapitalizedTargetClass = uncapitalize(getSimpleName(targetType));
    return MethodSpec.methodBuilder("with")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(ClassName.get(targetType), unCapitalizedTargetClass)
        .addStatement("return new $T()$L", builderClassName,
            targetExecutable.getParameters().stream()
                .map(p -> {
                  String pName = getSimpleName(p);
                  return new StringBuilder("\n.")
                      .append(pName).append("(")
                      .append(unCapitalizedTargetClass).append(".")
                      .append(isBooleanType(p) ? "is" : "get")
                      .append(capitalize(pName)).append("()")
                      .append(")")
                      .toString();
                }).collect(Collectors.joining()))
        .returns(builderClassName)
        .build();
  }

  /*
   * To create an "with(instance)", we need to make sure the target instance
   * has corresponding getters we can access and match up with our target
   * method/constructor.
   */
  private boolean canCreateWithInstance(TypeElement targetType, ExecutableElement targetExecutable) {
    for (VariableElement parameter : targetExecutable.getParameters()) {
      boolean hasGetterForParameter = false;
      String expectedMethodName = (isBooleanType(parameter) ? "is" : "get") + capitalize(getSimpleName(parameter));
      TypeMirror expectedMethodReturnType = parameter.asType();
      for (Element member : targetType.getEnclosedElements()) {
        if (member.getKind().equals(ElementKind.METHOD) &&
              ((ExecutableElement) member).getReturnType().equals(expectedMethodReturnType) &&
                getSimpleName(member).equals(expectedMethodName)) {
            hasGetterForParameter = true;
            break;
        }
      }
      if (!hasGetterForParameter)
        throw new ProcessingException(targetType, "'with' static factory method " +
            "requires getters for every (" + getSimpleName(targetExecutable) + ") " +
            "parameter. " + getSimpleName(targetType) + " has no getter for " +
            "parameter: " + getSimpleName(parameter));
    }
    return true;
  }

  /* Returns the generated Builder Class name depending on the given Element */
  private ClassName getBuilderClassName(TypeElement targetElement, Builder builder) {
    return getClassName(elementUtils.getPackageOf(targetElement),
        builder.prefix() + getSimpleName(targetElement) + builder.suffix());
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return new HashSet<>(Arrays.asList(Builder.class.getName()));
  }
}
