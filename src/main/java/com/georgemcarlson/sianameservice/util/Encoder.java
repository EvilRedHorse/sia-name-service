package com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.jbasen.BaseN;

public enum Encoder {
  BASE_8("23456789"),
  ;

  private byte[] encodingTable;

  Encoder(String encodingTable) {
    this.encodingTable = encodingTable.getBytes();
  }

  public byte[] encode(byte[] data) {
    try {
      return BaseN.getInstance(encodingTable).encode(data);
    } catch (Exception e) {
      return null;
    }
  }

  public byte[] decode(byte[] data) {
    try {
      return BaseN.getInstance(encodingTable).decode(data);
    } catch (Exception e) {
      return null;
    }
  }

}
