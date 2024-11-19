package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Moise Milenge
 * @author Tiffany Yan
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  private final int number;                 // Block number
  private final Transaction transaction;    // Transaction stored in the block
  private final Hash prevHash;              // Hash of the previous block
  private long nonce;                       // Nonce value for this block
  private Hash hash;                        // Hash of this block

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   * @throws IllegalArgumentException if the transaction is null.
   */
  public Block(int num, Transaction transaction, Hash prevHash, HashValidator check) {
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null.");
    }
    if (check == null) {
      throw new IllegalArgumentException("HashValidator cannot be null.");
    }

    this.number = num;
    this.transaction = transaction;
    this.prevHash = prevHash;
    this.nonce = 0;

    // Mining: Find a nonce that produces a valid hash
    do {
      this.nonce++;
      this.hash = computeHash();
    } while (!check.isValid(this.hash));
  }

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param nonce
   *   The nonce of the block.
   * @throws IllegalArgumentException if the transaction is null.
   */
  public Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null.");
    }

    this.number = num;
    this.transaction = transaction;
    this.prevHash = prevHash;
    this.nonce = nonce;
    this.hash = computeHash();
  }

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already
   * stored in the block.
   *
   * @return the computed hash.
   */
  private Hash computeHash() {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");

      // Add block number
      md.update(ByteBuffer.allocate(Integer.BYTES).putInt(this.number).array());

      // Add transaction data
      md.update(this.transaction.getSource().getBytes());
      md.update(this.transaction.getTarget().getBytes());
      md.update(ByteBuffer.allocate(Integer.BYTES).putInt(this.transaction.getAmount()).array());

      // Add previous hash
      if (this.prevHash != null) {
        md.update(this.prevHash.getBytes());
      }

      // Add nonce
      md.update(ByteBuffer.allocate(Long.BYTES).putLong(this.nonce).array());

      return new Hash(md.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found.", e);
    }
  }

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.number;
  }

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transaction;
  }

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  }

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  public Hash getPrevHash() {
    return this.prevHash;
  }

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  public Hash getHash() {
    return this.hash;
  }

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    return String.format(
        "Block %d (Transaction: %s, Nonce: %d, prevHash: %s, hash: %s)",
        this.number,
        this.transaction,
        this.nonce,
        this.prevHash == null ? "null" : this.prevHash.toString(),
        this.hash.toString()
    );
  }
} // class Block
