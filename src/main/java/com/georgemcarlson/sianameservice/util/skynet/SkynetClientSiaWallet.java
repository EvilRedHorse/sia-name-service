package com.georgemcarlson.sianameservice.util.skynet;

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

public class SkynetClientSiaWallet extends SkynetClient {
  private static final String URL_LINK_TEMPLATE = "http://localhost:4280/pubaccess/publink/%s";
  private static final String URL_FILE_TEMPLATE = "http://localhost:4280/pubaccess/pubfile/%s";
  private static final String USER_AGENT = "Sia-Agent";
  private static final SkynetClientSiaWallet SINGLETON = new SkynetClientSiaWallet();

  private SkynetClientSiaWallet() {

  }

  public static SkynetClientSiaWallet getSingletonInstance() {
    return SINGLETON;
  }

  @Override
  public String getName() {
    return "localhost";
  }

  public String getProtocol() {
    return "http";
  }

  @Override
  public void head(String skylink, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    Request.Builder requestBuilder = new Request.Builder();
    requestBuilder.url(String.format(URL_LINK_TEMPLATE, skylink));
    requestBuilder.header("User-Agent", USER_AGENT);
    requestBuilder.head();

    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
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

  @Override
  public void get(String skylink, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    Request.Builder requestBuilder = new Request.Builder();
    requestBuilder.url(String.format(URL_LINK_TEMPLATE, skylink));
    requestBuilder.header("User-Agent", USER_AGENT);

    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
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

  @Override
  public void store(String filename, String contentType, byte[] data, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    clientBuilder.authenticator((route, skynetResponse) -> {
      String credential = Credentials.basic("", Settings.getWalletApiPassword());
      return skynetResponse.request().newBuilder().header("Authorization", credential).build();
    }).connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS);

    RequestBody binary = RequestBody.create(
        okhttp3.MediaType.get(contentType),
        data
    );
    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("file", filename, binary)
        .build();
    Request.Builder requestBuilder = new Request.Builder();
    requestBuilder.url(String.format(URL_FILE_TEMPLATE, filename));
    requestBuilder.header("User-Agent", USER_AGENT);
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
