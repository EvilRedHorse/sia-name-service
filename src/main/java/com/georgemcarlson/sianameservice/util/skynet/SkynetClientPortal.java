package com.georgemcarlson.sianameservice.util.skynet;

import com.georgemcarlson.sianameservice.util.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SkynetClientPortal extends SkynetClient {

  private final Logger LOGGER = Logger.getInstance();
  private final String portal;

  private SkynetClientPortal(String portal) {
    this.portal = portal;
  }

  public static SkynetClientPortal getInstance(String portal) {
    return new SkynetClientPortal(portal);
  }

  @Override
  public String getName() {
    return portal;
  }

  @Override
  public String getProtocol() {
    try {
      Request.Builder requestBuilder = new Request.Builder();
      requestBuilder.url("http://" + portal);
      requestBuilder.head();

      OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
      if (clientBuilder.build().newCall(requestBuilder.build()).execute().request().isHttps()) {
        return "https";
      }
    } catch (Exception e) {
      LOGGER.error(e.getLocalizedMessage(), e);
    }
    return "http";
  }

  @Override
  public void head(final String path, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final String query = request.getQueryString() != null && !request.getQueryString().isEmpty()
        ? "?" + request.getQueryString()
        : "";
    final ByteBuffer bytes = ByteBuffer.wrap("".getBytes());
    final AsyncContext async = request.startAsync();
    final ServletOutputStream out = response.getOutputStream();
    out.setWriteListener(new WriteListener() {
      @Override
      public void onWritePossible() throws IOException {
        while (out.isReady()) {
          if (!bytes.hasRemaining()) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.setHeader("Location", getProtocol() + "://" + portal + "/" + path + query);
            async.complete();
            return;
          }
          out.write(bytes.get());
        }
      }

      @Override
      public void onError(Throwable t) {
        response.setStatus(500);
        async.complete();
      }
    });
  }

  @Override
  public void get(final String path, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final String query = request.getQueryString() != null && !request.getQueryString().isEmpty()
        ? "?" + request.getQueryString()
        : "";
    final ByteBuffer bytes = ByteBuffer.wrap("".getBytes());
    final AsyncContext async = request.startAsync();
    final ServletOutputStream out = response.getOutputStream();
    out.setWriteListener(new WriteListener() {
      @Override
      public void onWritePossible() throws IOException {
        while (out.isReady()) {
          if (!bytes.hasRemaining()) {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.setHeader("Location", getProtocol() + "://" + portal + "/" + path + query);
            async.complete();
            return;
          }
          out.write(bytes.get());
        }
      }

      @Override
      public void onError(Throwable t) {
        response.setStatus(500);
        async.complete();
      }
    });
  }

  @Override
  public void store(String filename, byte[] data, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final ByteBuffer bytes = ByteBuffer.wrap("".getBytes());
    final AsyncContext async = request.startAsync();
    final ServletOutputStream out = response.getOutputStream();
    out.setWriteListener(new WriteListener() {
      @Override
      public void onWritePossible() throws IOException {
        while (out.isReady()) {
          if (!bytes.hasRemaining()) {
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            System.out.println(getProtocol() + "://" + portal + request.getPathInfo());
            response.setHeader("Location", getProtocol() + "://" + portal + request.getPathInfo());
            async.complete();
            return;
          }
          out.write(bytes.get());
        }
      }

      @Override
      public void onError(Throwable t) {
        response.setStatus(500);
        async.complete();
      }
    });
  }

}
