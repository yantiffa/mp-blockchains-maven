package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;

/**
 * A full blockchain.
 *
 * @author Moise Milenge
 * @author Tiffany Yan
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Validator for hash validation.
   */
  private final HashValidator validator;

  /**
   * Head of the chain.
   */
  private Node head;

  /**
   * Tail of the chain.
   */
  private Node tail;

  /**
   * Number of blocks in the chain.
   */
  private int size;

  // A node in the blockchain
  private static class Node {
    /**
     * The data of the block.
     */
    Block data;

    /**
     * the node after the current block.
     */
    Node next;

    Node(Block datas, Node nexts) {
      this.data = datas;
      this.next = nexts;
    } // Node
  } // Node

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    if (check == null) {
      throw new IllegalArgumentException("HashValidator cannot be null.");
    } // if

    this.validator = check;

    // Create the genesis block
    Transaction initialTransaction = new Transaction("", "", 0);
    Block genesisBlock = new Block(0, initialTransaction, new Hash(new byte[] {}), validator);

    // Validate the genesis block
    if (!validator.isValid(genesisBlock.getHash())) {
      throw new IllegalStateException("Genesis block is invalid.");
    } // if

    this.head = new Node(genesisBlock, null);
    this.tail = head;
    this.size = 1;
  } // BlockChain

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  @Override
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      private Node current = head.next; // Skip genesis block

      @Override
      public boolean hasNext() {
        return current != null;
      } //hasNext()

      @Override
      public Transaction next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } //if
        Transaction transaction = current.data.getTransaction();
        current = current.next;
        return transaction;
      } //next()
    };
  } //iterator()

  /**
   * Mine for a new valid block for the end of the chain, returning that block.
   *
   * @param t The transaction that goes in the block.
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    if (t == null) {
      throw new IllegalArgumentException("Transaction cannot be null.");
    } //if

    // Validate the transaction (e.g., source must have sufficient balance)
    if (!t.getSource().isEmpty() && balance(t.getSource()) < t.getAmount()) {
      throw new IllegalArgumentException("Insufficient balance for source: " + t.getSource());
    } //if

    return new Block(size, t, tail.data.getHash(), validator);
  } //mine()

  /**
   * Get the number of blocks currently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return size;
  } //getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk The block to add to the end of the chain.
   * @throws IllegalArgumentException if the block is invalid or has incorrect hashes.
   */
  public void append(Block blk) {
    if (!blk.getPrevHash().equals(tail.data.getHash())) {
      throw new IllegalArgumentException("Previous hash mismatch.");
    } //if
    if (!validator.isValid(blk.getHash())) {
      throw new IllegalArgumentException("Block hash is invalid.");
    } //if
    tail.next = new Node(blk, null);
    tail = tail.next;
    size++;
  } //append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block, true otherwise.
   */
  public boolean removeLast() {
    if (size == 1) {
      return false;
    } //if
    Node current = head;
    while (current.next != tail) {
      current = current.next;
    } //while
    current.next = null;
    tail = current;
    size--;
    return true;
  } //removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last block in the chain.
   */
  public Hash getHash() {
    return tail.data.getHash();
  } //getHash()

  /**
   * Check if the blockchain is valid.
   *
   * @return true if the blockchain is correct, false otherwise.
   */
  public boolean isCorrect() {
    Node current = head;
    while (current.next != null) {
      Block currentBlock = current.data;
      Block nextBlock = current.next.data;

      // Validate hashes
      if (!nextBlock.getPrevHash().equals(currentBlock.getHash())) {
        return false;
      } //if
      if (!validator.isValid(currentBlock.getHash())) {
        return false;
      } //if

      // Validate the transaction
      Transaction t = currentBlock.getTransaction();
      if (!t.getSource().isEmpty() && balance(t.getSource()) < t.getAmount()) {
        return false;
      } //if

      current = current.next;
    } //while
    return true;
  } //isCorrect()

  /**
   * Ensure the blockchain is valid or throw an exception if it isn't.
   *
   * @throws Exception if the blockchain is invalid.
   */
  public void check() throws Exception {
    Node current = head;
    int blockNum = 0;

    while (current.next != null) {
      Block currentBlock = current.data;
      Block nextBlock = current.next.data;

      // Validate hashes
      if (!nextBlock.getPrevHash().equals(currentBlock.getHash())) {
        throw new Exception("Invalid previous hash at block " + blockNum);
      } //if
      if (!validator.isValid(currentBlock.getHash())) {
        throw new Exception("Invalid hash at block " + blockNum);
      } //if

      // Validate the transaction
      Transaction t = currentBlock.getTransaction();
      if (!t.getSource().isEmpty() && balance(t.getSource()) < t.getAmount()) {
        throw new Exception("Invalid transaction at block " + blockNum);
      } //if

      current = current.next;
      blockNum++;
    } //while
  } //check()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all users.
   */
  public Iterator<String> users() {
    Set<String> users = new HashSet<>();
    Node current = head;

    // Skip genesis block
    if (current != null) {
      current = current.next;
    } //if

    while (current != null) {
      Transaction transaction = current.data.getTransaction();
      if (!transaction.getSource().isEmpty()) {
        users.add(transaction.getSource());
      } //if
      users.add(transaction.getTarget());
      current = current.next;
    } //while
    return users.iterator();
  } //Iterator

  /**
   * Find one user's balance.
   *
   * @param user The user whose balance we want to find.
   * @return the user's balance.
   */
  public int balance(String user) {
    int balance = 0;
    Node current = head;

    // Skip genesis block
    if (current != null && current.data.getTransaction().getAmount() == 0) {
      current = current.next;
    } //if

    while (current != null) {
      Transaction transaction = current.data.getTransaction();
      if (transaction.getSource().equals(user)) {
        balance -= transaction.getAmount();
      } //if
      if (transaction.getTarget().equals(user)) {
        balance += transaction.getAmount();
      } //if
      current = current.next;
    } //while
    return balance;
  } //balance

  /**
   * Get an iterator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      private Node current = head;

      @Override
      public boolean hasNext() {
        return current != null;
      } //hasNext()

      @Override
      public Block next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } //if
        Block block = current.data;
        current = current.next;
        return block;
      } //next()
    };
  } //Iterator()
} // class BlockChain
