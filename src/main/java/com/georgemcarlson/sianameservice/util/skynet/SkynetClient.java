package com.georgemcarlson.sianameservice.util.skynet;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SkynetClient {

  SkynetClient() {

  }

  public abstract String getName();

  public abstract String getProtocol();

  public abstract void head(String path, final HttpServletRequest request, final HttpServletResponse response) throws IOException;

  public abstract void get(String path, final HttpServletRequest request, final HttpServletResponse response) throws IOException;

  public abstract void store(String filename, byte[] data, final HttpServletRequest request, final HttpServletResponse response) throws IOException;

}
