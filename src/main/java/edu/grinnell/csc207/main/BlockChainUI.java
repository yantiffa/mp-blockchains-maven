package edu.grinnell.csc207.main;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;
import edu.grinnell.csc207.util.IOUtils;

/**
 * A simple UI for our BlockChain class.
 * This is written for csc207 fall 2024
 *
 * @author Moise Milenge
 * @author Tiffany Yan
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 3;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
        Valid commands:
          mine: discovers the nonce for a given transaction
          append: appends a new block onto the end of the chain
          remove: removes the last block from the end of the chain
          check: checks that the block chain is valid
          users: prints a list of users
          balance: finds a user's balance
          transactions: prints out the chain of transactions
          blocks: prints out the chain of blocks (for debugging only)
          help: prints this list of commands
          quit: quits the program""");
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args Command-line arguments (currently ignored).
   */
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator = (h) -> {
      if (h.length() < VALIDATOR_BYTES) {
        return false;
      } // if
      for (int v = 0; v < VALIDATOR_BYTES; v++) {
        if (h.get(v) != 0) {
          return false;
        } // if
      } // for
      return true;
    };
    BlockChain chain = new BlockChain(validator);

    instructions(pen);

    boolean done = false;

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      } // if

      switch (command.toLowerCase()) {
        case "mine":
          String source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          String target = IOUtils.readLine(pen, eyes, "Target: ");
          int amount = IOUtils.readInt(pen, eyes, "Amount: ");
          Block minedBlock = chain.mine(new Transaction(source, target, amount));
          pen.println("Mined block with nonce: " + minedBlock.getNonce());
          break;

        case "append":
          Block blockToAppend = chain.mine(new Transaction("Mined", "System", 0)); // Example
          chain.append(blockToAppend);
          pen.println("Block appended to the chain.");
          break;

        case "remove":
          boolean removed = chain.removeLast();
          if (removed) {
            pen.println("Last block removed from the chain.");
          } else {
            pen.println("Cannot remove the last block (only one block in the chain).");
          } // if
          break;

        case "check":
          try {
            chain.check();
            pen.println("Blockchain is valid.");
          } catch (Exception e) {
            pen.println("Blockchain is invalid: " + e.getMessage());
          } // try/catch
          break;

        case "users":
          pen.println("Users in the blockchain:");
          Iterator<String> userIterator = chain.users();
          while (userIterator.hasNext()) {
            pen.println("- " + userIterator.next());
          } // while
          break;

        case "balance":
          String user = IOUtils.readLine(pen, eyes, "Enter the username to check balance: ");
          int balance = chain.balance(user);
          pen.println(user + "'s balance: " + balance);
          break;

        case "transactions":
          pen.println("Transactions in the blockchain:");
          for (Transaction transaction : chain) {
            pen.println("- " + transaction);
          } // for
          break;

        case "blocks":
          pen.println("Blocks in the blockchain:");
          Iterator<Block> blockIterator = chain.blocks();
          while (blockIterator.hasNext()) {
            pen.println(blockIterator.next());
          } // while
          break;

        case "help":
          instructions(pen);
          break;

        case "quit":
          done = true;
          break;

        default:
          pen.printf("Invalid command: '%s'. Try again.\n", command);
          break;
      } // switch
    } // while

    pen.printf("\nGoodbye\n");
    eyes.close();
    pen.close();
  } // main(String[])
} // class BlockChainUI
