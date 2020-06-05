package com.georgemcarlson.sianameservice.util.skynet;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    clientBuilder.authenticator((route, skynetResponse) -> {
      String credential = Credentials.basic("", Settings.WALLET_API_PASSWORD);
      return skynetResponse.request().newBuilder().header("Authorization", credential).build();
    }).connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS);

    RequestBody binary = RequestBody.create(
        okhttp3.MediaType.get("text/plain; charset=utf-8"),
        data
    );
    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("file", filename, binary)
        .build();
    Request.Builder requestBuilder = new Request.Builder();
    requestBuilder.url(
        String.format(getProtocol() + "://" + portal + request.getPathInfo(), filename)
    );
    requestBuilder.post(body);

    Response skynetResponse = clientBuilder.build().newCall(requestBuilder.build()).execute();
    final ByteBuffer bytes = ByteBuffer.wrap(skynetResponse.body().bytes());
    final Headers headers = skynetResponse.headers();
    final AsyncContext async = request.startAsync();
    final ServletOutputStream out = response.getOutputStream();
    out.setWriteListener(new WriteListener() {
      @Override
      public void onWritePossible() throws IOException {
        while (out.isReady()) {
          if (!bytes.hasRemaining()) {
            for (String name : headers.names()) {
              response.setHeader(name, headers.get(name));
            }
            response.setStatus(200);
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
