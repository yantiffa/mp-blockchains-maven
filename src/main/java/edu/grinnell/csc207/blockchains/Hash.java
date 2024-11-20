package edu.grinnell.csc207.blockchains;

import java.lang.StringBuilder;
import java.util.Arrays;

/**
 * Encapsulated hashes.
 * This is written for csc207 fall 2024
 *
 * @author Tiffany
 * @author Moses
 * @author Samuel A. Rebelsky
 */
public class Hash {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * Array that stores byte of array.
   */
  private byte[] data;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new encapsulated hash.
   *
   * @param datas The data to copy into the hash.
   */
  public Hash(byte[] datas) {
    byte[] copy = new byte[datas.length];
    for (int i = 0; i < datas.length; i++) {
      copy[i] = datas[i];
    } // for
    this.data = copy;
  } // Hash(byte[])

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Determine how many bytes are in the hash.
   *
   * @return the number of bytes in the hash.
   */
  public int length() {
    return this.data.length;
  } // length()

  /**
   * Get the ith byte.
   *
   * @param i The index of the byte to get, between 0 (inclusive) and length() (exclusive).
   *
   * @return the ith byte
   */
  public byte get(int i) {
    return this.data[i];
  } // get()

  /**
   * Get a copy of the bytes in the hash. We make a copy so that the client cannot change them.
   *
   * @return a copy of the bytes in the hash.
   */
  public byte[] getBytes() {
    byte[] copy = new byte[data.length];
    for (int i = 0; i < data.length; i++) {
      copy[i] = data[i];
    } // for
    return copy;
  } // getBytes()

  /**
   * Convert to a hex string.
   *
   * @return the hash as a hex string.
   */
  public String toString() {
    StringBuilder outcome = new StringBuilder("");
    for (byte b : this.data) {
      outcome.append(String.format("%02X", b));
    } // for
    return outcome.toString();
  } // toString()

  /**
   * Determine if this is equal to another object.
   *
   * @param other The object to compare to.
   *
   * @return true if the two objects are conceptually equal and false otherwise.
   */
  public boolean equals(Object other) {
    if (!(other instanceof Hash)) {
      return false;
    } // if
    Hash otherHash = (Hash) other;
    if (Arrays.equals(this.data, otherHash.data)) {
      return true;
    } // if
    return false;
  } // equals(Object)

  /**
   * Get the hash code of this object.
   *
   * @return the hash code.
   */
  public int hashCode() {
    return this.toString().hashCode();
  } // hashCode()
} // class Hash
