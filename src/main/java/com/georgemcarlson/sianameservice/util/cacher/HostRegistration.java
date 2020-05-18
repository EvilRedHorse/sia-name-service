package com.georgemcarlson.sianameservice.util.cacher;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import java.util.Arrays;

public class HostRegistration {

  private static final Logger LOGGER = Logger.getInstance();
  private static final HostRegistration INVALID_SINGLETON
      = new HostRegistration(false, null, null, -1, null);
  private final boolean isValid;
  private final String skyLink;
  private final String host;
  private final int blockSeconds;
  private final TxOutput registrant;

  private HostRegistration(boolean isValid, String skyLink, String host, int blockSeconds, TxOutput registrant) {
    this.isValid = isValid;
    this.skyLink = skyLink;
    this.host = host;
    this.blockSeconds = blockSeconds;
    this.registrant = registrant;
  }

  public static HostRegistration getInvalidSingletonInstance() {
    return INVALID_SINGLETON;
  }

  public static HostRegistration getInstance(byte[] arbitraryData, TxOutput fee) {
    if (invalid(arbitraryData)) {
      return INVALID_SINGLETON;
    }
    return new HostRegistration(
        true,
        parseSkyLink(arbitraryData),
        parseHost(arbitraryData),
        parseBlockSeconds(arbitraryData),
        fee
    );
  }

  private static boolean invalid(byte[] arbitraryData) {
    if (arbitraryData == null
        || arbitraryData.length < Settings.SKYLINK_LENGTH + ("a.b ").length()
        || arbitraryData.length > Settings.SKYLINK_LENGTH + ("99999 a.b ").length() + 50) {
      return true;
    }
    for (String tld : Settings.TLDS) {
      if (arbitraryData.length < Settings.SKYLINK_LENGTH + ("." + tld + " ").length()) {
        continue;
      }
      if (Arrays.equals(("." + tld).getBytes(),
          Arrays.copyOfRange(
              arbitraryData,
              arbitraryData.length - Settings.SKYLINK_LENGTH - ("." + tld + " ").length(),
              arbitraryData.length - Settings.SKYLINK_LENGTH - " ".length()
          )
        )
      ) {
        return false;
      }
    }
    return true;
  }

  public boolean isValid() {
    return this.isValid;
  }

  public static String parseSkyLink(byte[] arbitraryData) {
    return new String(
        Arrays.copyOfRange(
            arbitraryData,
            arbitraryData.length - Settings.SKYLINK_LENGTH,
            arbitraryData.length
        )
    );
  }

  public static String parseHost(byte[] arbitraryData) {
    String host = new String(
        Arrays.copyOfRange(
            arbitraryData,
            0,
            arbitraryData.length - Settings.SKYLINK_LENGTH - " ".length()
        )
    );
    if (host.contains(" ")) {
      host = host.substring(host.indexOf(" ") + 1);
    }
    return host;
  }

  public static int parseBlockSeconds(byte[] arbitraryData) {
    String blockSeconds = new String(
        Arrays.copyOfRange(
            arbitraryData,
            0,
            arbitraryData.length - Settings.SKYLINK_LENGTH - " ".length()
        )
    );
    if (!blockSeconds.contains(" ")) {
      return -1;
    }
    try {
      return Integer.parseInt(blockSeconds.substring(blockSeconds.indexOf(" ") + 1));
    } catch (Exception e) {
      LOGGER.error(e);
      return -1;
    }
  }

  public String getSkyLink() {
    return skyLink;
  }

  public String getHost() {
    return host;
  }

  public int getBlockSeconds() {
    return blockSeconds;
  }

  public TxOutput getRegistrant() {
    return registrant;
  }

}
