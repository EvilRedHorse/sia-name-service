package com.georgemcarlson.sianameservice.util;

public class Logger {

  private static final Logger SINGLETON = new Logger();

  private Logger() {

  }

  public static Logger getInstance() {
    return SINGLETON;
  }

  public void info(String message) {
    System.out.println(message);
  }

  public void error(Exception e) {
    error(e.getLocalizedMessage(), e);
  }

  public void error(String message, Exception e) {
    System.out.println(message);
    for (int i = 0; i < e.getStackTrace().length; i++) {
      System.out.println("  " + e.getStackTrace()[i]);
    }
  }

}
