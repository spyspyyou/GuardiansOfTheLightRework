import java.io.*;
import java.math.BigInteger;

public class ReadBinary {

    public static void main(String args[])
    {
        try {
            BigInteger bnt = new BigInteger(1, getBytesFromFile(new File("C:\\Users\\Sandro\\Desktop\\Zhang_Yong.bin")));
            System.out.printf(bnt.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.printf("failed");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("failed");
        }
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }

}
