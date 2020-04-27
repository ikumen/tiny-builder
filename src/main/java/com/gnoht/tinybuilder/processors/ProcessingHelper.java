package com.gnoht.tinybuilder.processors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;

/**
 * @author ikumen@gnoht.com
 */
public class ProcessingHelper {
  /**
   * Capitalizes given input. Assumes input is just 1 word, does not tokenize
   * into separate words to capitalize.
   *
   * @param s word to capitalize.
   * @return null if s is null, "" if s is "", otherwise s with first character in uppercase.
   */
  public static String capitalize(String s) {
    if (s == null || s.length() == 0)
      return s;

    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  /**
   * Un-capitalize given input. Assumes input is just 1 word, does not tokenize
   * input into separate words to un-capitalize.
   *
   * @param s word to un-capitalize.
   * @return null if s is null, "" if s is "", otherwise s with first character in lowercase.
   */
  public static String uncapitalize(String s) {
    if (s == null || s.length() == 0)
      return s;

    return Character.toLowerCase(s.charAt(0)) + s.substring(1);
  }

  /**
   * Return true if a given {@link Element} is boolean primitive or boxed version.
   *
   * @param element
   * @return true if boolean or Boolean, otherwise false.
   */
  public static boolean isBooleanType(Element element) {
    TypeName typeName = getTypeName(element);
    return (typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.BOOLEAN))
        || (typeName.isPrimitive() && typeName.equals(TypeName.BOOLEAN));
  }

  public static TypeName getTypeName(Element element) {
    return TypeName.get(element.asType());
  }

  public static String getQualifiedName(QualifiedNameable qualifiedNameable) {
    return qualifiedNameable.getQualifiedName().toString();
  }

  public static ClassName getClassName(PackageElement packageElement, String name, String ...names) {
    return ClassName.get(getQualifiedName(packageElement), name, names);
  }

  public static String getSimpleName(Element element) {
    return element.getSimpleName().toString();
  }
}
