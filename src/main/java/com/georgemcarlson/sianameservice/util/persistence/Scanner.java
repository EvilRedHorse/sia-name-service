package com.georgemcarlson.sianameservice.util.persistence;

import com.georgemcarlson.sianameservice.util.Logger;
import java.sql.SQLException;
import java.util.List;
import org.json.JSONObject;

public class Scanner {
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
              "CREATE CACHED TABLE scanner (",
              "  id INTEGER IDENTITY,",
              "  block INTEGER",
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
  private int block = -1;

  public static Scanner findById(int id) {
    try {
      List<String> result = EntityController.getSingletonInstance().query(
          String.join(
              " ",
              "SELECT block",
              "FROM scanner",
              "WHERE id = :id",
              "LIMIT 1")
              .replace(":id", String.valueOf(id))
      ).get(0);
      Scanner scanner = new Scanner();
      scanner.setId(id);
      scanner.setBlock(Integer.parseInt(result.get(0)));
      return scanner;
    } catch(Exception e) {
      return null;
    }
  }

  public static Scanner findHighest() {
    try {
      List<String> result = EntityController.getSingletonInstance().query(
          String.join(
              " ",
              "SELECT id, block",
              "FROM scanner",
              "ORDEr BY block DESC",
              "LIMIT 1")
      ).get(0);
      Scanner scanner = new Scanner();
      scanner.setId(Integer.parseInt(result.get(0)));
      scanner.setBlock(Integer.parseInt(result.get(1)));
      return scanner;
    } catch(Exception e) {
      return null;
    }
  }

  public void create() throws SQLException {
    EntityController.getSingletonInstance().update(
        String.join(
            " ",
            "INSERT INTO scanner (block)",
            "VALUES(:block)")
            .replace(":block", String.valueOf(block))
    );
    id = Integer.valueOf(
        EntityController.getSingletonInstance().query("CALL IDENTITY()").get(0).get(0)
    );
  }

  public void update() throws SQLException {
    EntityController.getSingletonInstance().update(
        String.join(
            " ",
            "UPDATE scanner",
            "SET",
            "block = :block",
            "WHERE id = :id")
            .replace(":id", String.valueOf(id))
            .replace(":block", String.valueOf(block))
    );
  }

  public String toString(int indentFactor) {
    JSONObject scanner = new JSONObject();
    scanner.put("id", id);
    scanner.put("block", block);
    return scanner.toString(indentFactor);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getBlock() {
    return block;
  }

  public void setBlock(int block) {
    this.block = block;
  }

}
