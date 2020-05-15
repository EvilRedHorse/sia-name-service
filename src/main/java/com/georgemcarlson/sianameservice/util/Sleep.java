package com.georgemcarlson.sianameservice.util;

import java.util.concurrent.TimeUnit;

public class Sleep {
  private static final Logger LOGGER = Logger.getInstance();

  public static void block(long amount, TimeUnit timeUnit) {
    try {
      Thread.sleep(timeUnit.toMillis(amount));
    } catch (Exception e) {
      LOGGER.error(e.getLocalizedMessage(), e);
    }
  }

}
