package com.georgemcarlson.sianameservice.util.reader.user;

public enum ClientType {
  THICK_CLIENT("thickclient"),
  THIN_CLIENT("thinclient"),
  ;

  private String value;

  ClientType(String value) {
    this.value = value;
  }

  public static ClientType of(String value) {
    for (ClientType clientType : ClientType.values()) {
      if (clientType.value.equals(value)) {
        return clientType;
      }
    }
    return null;
  }

}
