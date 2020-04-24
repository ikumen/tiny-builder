package com.gnoht.biruda.processors;

import javax.lang.model.element.Element;

/**
 * @author ikumen@gnoht.com
 */
public class ProcessingException extends RuntimeException {
  private Element element;
  private String msg;
  private Throwable cause;

  public ProcessingException(Element element, String msg, Object...args) {
    super(String.format(msg, args));
    this.element = element;
  }

  public ProcessingException(Element element, Throwable cause, String msg, Object...args) {
    super(String.format(msg, args), cause);
    this.element = element;
  }

}
