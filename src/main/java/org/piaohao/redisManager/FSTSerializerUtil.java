package org.piaohao.redisManager;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/24.
 */
public class FSTSerializerUtil {

//    private static final Log logger = Log.getLog(FSTSerializerUtil.class);
    // Serialize
    //-----------------------------------------------------------------------

    //    In order to optimize object reuse and thread safety,
    // FSTConfiguration provides 2 simple factory methods to
    // obtain input/outputstream instances (they are stored thread local):
    //! reuse this Object, it caches metadata. Performance degrades massively
    //using createDefaultConfiguration()        FSTConfiguration is singleton

    /**
     * <p>Serializes an <code>Object</code> to a byte array for
     * storage/serialization.</p>
     *
     * @param obj the object to serialize to bytes
     * @return a byte[] with the converted Serializable
     */
    public static byte[] fastSerialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FSTObjectOutput out = null;
        try {
            // stream closed in the finally
            byteArrayOutputStream = new ByteArrayOutputStream(512);
            out = new FSTObjectOutput(byteArrayOutputStream);  //32000  buffer size
            out.writeObject(obj);
            out.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();    //call flush byte buffer
                }
                if (byteArrayOutputStream != null) {

                    byteArrayOutputStream.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
    // Deserialize
    //-----------------------------------------------------------------------

    /**
     * <p>Deserializes a single <code>Object</code> from an array of bytes.</p>
     *
     * @param objectData the serialized object, must not be null
     * @return the deserialized object
     * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
     */
    public static Object fastDeserialize(byte[] objectData) {
        ByteArrayInputStream byteArrayInputStream = null;
        FSTObjectInput in = null;
        try {
            // stream closed in the finally
            byteArrayInputStream = new ByteArrayInputStream(objectData);
            in = new FSTObjectInput(byteArrayInputStream);
            return  in.readObject();
        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
}
