package com.georgemcarlson.sianameservice.util.persistence;

import com.georgemcarlson.sianameservice.util.Logger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

public class WhoIs {
  private static final Logger LOGGER = Logger.getInstance();
  static {
    /*
     * Make an empty table if one does not already exist. Declaring the id column IDENTITY so that
     * the db will automatically generate unique values for new rows.
     */
    try {
      EntityController.getSingletonInstance().update(
          String.join(
              " ",
              "CREATE CACHED TABLE who_is (",
              "  id INTEGER IDENTITY,",
              "  host VARCHAR(100),",
              "  skylink VARCHAR(46),",
              "  registrant VARCHAR(76),",
              "  fee INTEGER,",
              "  block INTEGER,",
              "  seconds INTEGER",
              ")"
          )
      );
    } catch (SQLException e) {
      /*
       * Ignore. This will throw an exception when the program is run for a second time as the
       * database will already exist. This will have no effect on the database.
       */
    }
  }
  private Integer id;
  private String host = "";
  private String skylink = "";
  private String registrant = "";
  private int fee = -1;
  private int block = -1;
  private int seconds = -1;

  public static WhoIs findById(int id) {
    try {
      List<String> result = EntityController.getSingletonInstance().query(
          String.join(
              " ",
              "SELECT host, skylink, registrant, fee, block, seconds",
              "FROM who_is",
              "WHERE id = :id",
              "LIMIT 1")
              .replace(":id", String.valueOf(id))
      ).get(0);
      WhoIs whoIs = new WhoIs();
      whoIs.setId(id);
      whoIs.setHost(result.get(0));
      whoIs.setSkylink(result.get(1));
      whoIs.setRegistrant(result.get(2));
      whoIs.setFee(Integer.parseInt(result.get(3)));
      whoIs.setBlock(Integer.parseInt(result.get(4)));
      whoIs.setSeconds(Integer.parseInt(result.get(5)));
      return whoIs;
    } catch(Exception e) {
      return null;
    }
  }

  public static WhoIs findByHost(String host) {
    try {
      List<String> result = EntityController.getSingletonInstance().query(
          String.join(
              " ",
              "SELECT id, skylink, registrant, fee, block, seconds",
              "FROM who_is",
              "WHERE host = ':host'",
              "ORDER BY id ASC",
              "LIMIT 1")
              .replace(":host", host)
      ).get(0);
      WhoIs whoIs = new WhoIs();
      whoIs.setId(Integer.parseInt(result.get(0)));
      whoIs.setHost(host);
      whoIs.setSkylink(result.get(1));
      whoIs.setRegistrant(result.get(2));
      whoIs.setFee(Integer.parseInt(result.get(3)));
      whoIs.setBlock(Integer.parseInt(result.get(4)));
      whoIs.setSeconds(Integer.parseInt(result.get(5)));
      return whoIs;
    } catch(Exception e) {
      return null;
    }
  }

  public static List<WhoIs> list(int offset) {
    try {
      List<List<String>> results = EntityController.getSingletonInstance().query(
          String.join(
              " ",
              "SELECT id, host, skylink, registrant, fee, block, seconds",
              "FROM who_is",
              "ORDER BY block DESC",
              "LIMIT 50",
              "OFFSET :offset")
              .replace(":offset", String.valueOf(offset))
      );
      List<WhoIs> whoAre = new ArrayList<>();
      for (List<String> result : results) {
        WhoIs whoIs = new WhoIs();
        whoIs.setId(Integer.parseInt(result.get(0)));
        whoIs.setHost(result.get(1));
        whoIs.setSkylink(result.get(2));
        whoIs.setRegistrant(result.get(3));
        whoIs.setFee(Integer.parseInt(result.get(4)));
        whoIs.setBlock(Integer.parseInt(result.get(5)));
        whoIs.setSeconds(Integer.parseInt(result.get(6)));
        whoAre.add(whoIs);
      }
      return whoAre;
    } catch(Exception e) {
      return Collections.emptyList();
    }
  }

  public void create() throws SQLException {
    EntityController.getSingletonInstance().update(
        String.join(
            " ",
            "INSERT INTO who_is (host, skylink, registrant, fee, block, seconds)",
            "VALUES(':host', ':skylink', ':registrant', :fee, :block, :seconds)")
            .replace(":host", host)
            .replace(":skylink", skylink)
            .replace(":registrant", registrant)
            .replace(":fee", String.valueOf(fee))
            .replace(":block", String.valueOf(block))
            .replace(":seconds", String.valueOf(seconds))
    );
    id = Integer.valueOf(
        EntityController.getSingletonInstance().query("CALL IDENTITY()").get(0).get(0)
    );
  }

  public void update() throws SQLException {
    EntityController.getSingletonInstance().update(
        String.join(
            " ",
            "UPDATE who_is",
            "SET",
            "host = ':host',",
            "skylink = ':skylink',",
            "registrant = ':registrant',",
            "fee = :fee,",
            "block = :block,",
            "seconds = :seconds",
            "WHERE id = :id")
            .replace(":id", String.valueOf(id))
            .replace(":host", host)
            .replace(":skylink", skylink)
            .replace(":registrant", registrant)
            .replace(":fee", String.valueOf(fee))
            .replace(":block", String.valueOf(block))
            .replace(":seconds", String.valueOf(seconds))
    );
  }

  public String toString(int indentFactor) {
    JSONObject whoIs = new JSONObject();
    whoIs.put("id", id);
    whoIs.put("host", host);
    whoIs.put("skylink", skylink);
    whoIs.put("registrant", registrant);
    whoIs.put("fee", fee);
    whoIs.put("block", block);
    whoIs.put("seconds", seconds);
    return whoIs.toString(indentFactor);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getSkylink() {
    return skylink;
  }

  public void setSkylink(String skylink) {
    this.skylink = skylink;
  }

  public String getRegistrant() {
    return registrant;
  }

  public void setRegistrant(String registrant) {
    this.registrant = registrant;
  }

  public int getFee() {
    return fee;
  }

  public void setFee(int fee) {
    this.fee = fee;
  }

  public int getBlock() {
    return block;
  }

  public void setBlock(int block) {
    this.block = block;
  }

  public int getSeconds() {
    return seconds;
  }

  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }
}
