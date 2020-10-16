/*
 * Bryce Jensen
 * 10/09/2020
 *
 *  openjdk 11.0.1 2018-10-16 LTS
 *  to compile:
 *      $ javac BlockchainA.java
 *
 *  to run, in one shell:
 *     $ java BlockchainA
 *
 *  Files needed to run:
 *                     a. checklist-block.html
 *                     b. Blockchain.java
 *                     c. BlockchainLog.txt
 *                     d. BlockchainLedgerSample.json
 *                     e. BlockInput0.txt
 *                     f. BlockInput1.txt
 *                     g. BlockInput2.txt
 *
 * Notes:
 *       This is mini-project A of the Blockchain assignment.
 *
 *       It contains a simple blockchain program with five nodes. A dummy genesis block and
 *       four other simple blocks.
 *
 *       Each block contains some arbitrary data, the hash of the previous block,
 *       a timestamp of its creation, and the hash of the block itself.
 *
 *       When calculating the hash for each block, the contained elements in the block
 *       are turned into strings and concatenated together with a nonce to then be hashed.
 *
 *       The verifying of blocks is done by taking in the block hash prefix and trying every possible
 *       combination by incrementing our nonce  until our prefixString is equal to our designated prefix
 *
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BlockchainA
{
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int  nonce;
    // declaration of private member variables

    /*
     * public constructor for BlockchainA
     * @param data var of type String
     * @param previousHash var of type String
     * @param timeStamp variable of type long
     */
    public BlockchainA(String data, String previousHash, long timeStamp)
    {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        // getters and setters
        this.hash = calculateBlockHash();
        // assigns hash to itself
    }


    public String calculateBlockHash()
    // method to calculate hash for current block
    {
        String dataToHash = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data;
        // concatenation of hash of the previous tx ,time of tx, the tx nonce, ans the tx data
        MessageDigest digest = null;
        // declare new message digest objecgt and isntatntiate to null
        byte[] bytes = null;
        // declare and initialize a new byte array

        try
        {
            digest = MessageDigest.getInstance("SHA-256");
            // get an instance of the SHA256 hashing algorithm and store it in digest
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
            // generate the hash value of our input data and stick in in our new byte array
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException exception)
        {
            System.err.println("Exception found: " + exception);
            exception.printStackTrace();
            // print exceptions to console
        }

        StringBuffer buffer = new StringBuffer();
        // declare and initialize anew string buffer
        for (byte b: bytes)
        // cycle through all bytes in bytes
        {
            buffer.append(String.format("%02x", b));
            // turn said byte into a hex string
        }
        return buffer.toString();
        // return our string buffer that now holds our hash
    }


    /*
     * method for mining a new block
     * @param a prefix var of type integer
     *
     * please note that this implementation does not verifying any date which
     * is a crucial component of blockchains with real-world application
     */
    public String mineBlock(int prefix)
    {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        /*
         * declare and intialialize our prefix string to a new string containing our prefix integer with '\0' replaced
         * by '0' to represent the prefix we are looking for
         */

        while (!hash.substring(0, prefix).equals(prefixString))
        // while we do not have our desired solution
        {
            nonce++;
            // increment our nonce
            hash = calculateBlockHash();
            // and calculate the hash
        }
        return hash;
        // return our winning hash w=once we find our desired prefixString
    }

    public String getHash()
    {
        return this.hash;
        // getter to return hash
    }

    public String getPreviousHash()
    {
        return this.previousHash;
        // getter to return previous hash
    }

    public String getData()
    {
        return this.data;
    }

    public void sendData(String data)
    {
        this.data = data;
        // method to send data to the block
    }

    public static void main(String a[])
    {
        List<BlockchainA> blockchain = new ArrayList<>();
        // declare and initialize our new blockchain
        int prefix = 4;
        // declare and initialize our prefix value to 4 leading zeroes
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        // declare and initialize our prefixString for this instance

        BlockchainA genesisBlock = new BlockchainA("This is the genesis Dummy Block.", "0", new Date().getTime());
        // declare and initialize a new genesis block to be our Dummy Block
        genesisBlock.mineBlock(prefix);
        // mine our Dummy Block
        blockchain.add(genesisBlock);
        // add it to our blockchain

        BlockchainA firstBlock = new BlockchainA("This is the first Simple Block.", genesisBlock.getHash(), new Date().getTime());
        // declare and initialize our first Simple Block
        firstBlock.mineBlock(prefix);
        // mine our first Simple block
        blockchain.add(firstBlock);
        // add it to our blockchain

        BlockchainA secondBlock = new BlockchainA("This is the second Simple Block.", firstBlock.getHash(), new Date().getTime());
        // declare and initialize our second Simple Block
        secondBlock.mineBlock(prefix);
        // mine our second Simple block
        blockchain.add(secondBlock);
        // add it to our blockchain

        BlockchainA thirdBlock = new BlockchainA("This is the third Simple Block.", secondBlock.getHash(), new Date().getTime());
        // declare and initialize our third Simple Block
        thirdBlock.mineBlock(prefix);
        // mine our third Simple block
        blockchain.add(thirdBlock);
        // add it to our blockchain

        BlockchainA fourthBlock = new BlockchainA("This is the fourth Simple Block.", blockchain.get(blockchain.size() - 1).getHash(), new Date().getTime());
        // declare and initialize our fourth Simple Block
        fourthBlock.mineBlock(prefix);
        // mine our fourth Simple block
        blockchain.add(fourthBlock);
        // add it to our blockchain

        boolean flag = true;
        // declare and initialize our boolean flag var to true
        String tempData = null;
        // declare adn initialize a tempData var to null
        String tempCurrentHash = null;
        // declare and initialize a tempCurrentHash var to null

        for (int i = 0; i < blockchain.size(); i++)
        // cycle through the size of the chain
        {
            String previousHash = i==0 ? "0" : blockchain.get(i - 1).getHash();
            flag = blockchain.get(i).getHash().equals(blockchain.get(i).calculateBlockHash()) &&
                    previousHash.equals((blockchain.get(i).getPreviousHash())) && blockchain.get(i).getHash().substring(0, prefix).equals(prefixString);
            /*
             * set flag equal to the boolean value if the stored hash for the current block is calculated and stored correctly
             * and if the previous block stored in the current block is actually the hash of the previous block.
             * and if the current block has been mined
             */

            tempCurrentHash = blockchain.get(i).getHash();
            // save current hash to print out
            tempData = blockchain.get(i).getData();
            // save data to tempData to print out

            if (!flag)
            {
                break;
            }
        }
        System.out.println("\nFlag: " + flag + "\nBlock: " + tempCurrentHash + "\nContent: " + tempData + "\nGood Job!\n");
        // print out the results to the console
    }
}
