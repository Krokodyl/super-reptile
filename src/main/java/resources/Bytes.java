package resources;

import satellaview.Converter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bytes {

    public static void writeBytes(byte[] source, byte[] target, int targetOffset) {
        for (byte b : source) {
            target[targetOffset++]=b;
        }
    }

    public static int lastIndexOf(byte[] array, byte[] target) {
        if (target.length == 0) {
            return array.length - 1;
        } else if (target.length > array.length) {
            return -1;
        }

        int lastIndexOf = -1;
        boolean differentValue;

        for (int i = 0; i <= array.length - target.length; i++) {
            differentValue = false;
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    differentValue = true;
                    break;
                }
            }
            if (!differentValue) {
                lastIndexOf = i;
            }
        }

        return lastIndexOf;
    }

    public static void saveData(String output, byte[] data) {
        System.out.println("Saving data : "+output);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(output);
            stream.write(data);
            stream.flush();
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stream != null) {
                try {
                    stream.flush();
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Bytes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static Object[][] splitArray(Byte[] arrayToSplit, int chunkSize){
        if(chunkSize<=0){
            return null;  // just in case :)
        }
        // first we have to check if the array can be split in multiple 
        // arrays of equal 'chunk' size
        int rest = arrayToSplit.length % chunkSize;  // if rest>0 then our last array will have less elements than the others 
        // then we check in how many arrays we can split our input array
        int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0); // we may have to add an additional array for the 'rest'
        // now we know how many arrays we need and create our result array
        Object[][] arrays = new Object[chunks][];
        // we create our resulting arrays by copying the corresponding 
        // part from the input array. If we have a rest (rest>0), then
        // the last array will have less elements than the others. This 
        // needs to be handled separately, so we iterate 1 times less.
        for(int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++){
            // this copies 'chunk' times 'chunkSize' elements into a new array
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
        }
        if(rest > 0){ // only when we have a rest
            // we copy the remaining elements into the last chunk
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }
        return arrays; // that's it
    }
}
