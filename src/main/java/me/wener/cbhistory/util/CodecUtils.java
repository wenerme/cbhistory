package me.wener.cbhistory.util;


import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;

public class CodecUtils
{
    private final static Gson gson = new Gson();

    public static Map<String, String> jsonToMap(String json)
    {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static <T> T jsonMergeTo(String json, T instance)
    {
        Gson gs = new GsonBuilder().registerTypeAdapter(instance.getClass(), new InstanceCreatorWithInstance<>(instance)).create();
        gs.fromJson(json, instance.getClass());
        return instance;
    }

    public static Date jsonToDate(String json)
    {
        boolean wrap = true;
        if (json.startsWith("\""))
            wrap = false;
        return jsonToDate(json, wrap);
    }
    public static Date jsonToDate(String json, boolean wrapQuote)
    {
        if (wrapQuote)
            json = "\""+json+"\"";
        return gson.fromJson(json, Date.class);
    }


    public static String decodeBase64(String base64)
    {
        return decodeBase64(base64, Charset.forName("UTF-8"));
    }

    public static String decodeBase64(String base64, Charset charset)
    {
        return new String(DatatypeConverter.parseBase64Binary(base64),charset);
    }
    public static String encodeBase64(String text)
    {
        return encodeBase64(text, Charsets.UTF_8);
    }
    public static String encodeBase64(String text, Charset charset)
    {
        return DatatypeConverter.printBase64Binary(text.getBytes(charset));
    }

    /**
     * Decodes the passed UTF-8 String using an algorithm that's compatible with
     * JavaScript's <code>decodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The UTF-8 encoded String to be decoded
     * @return the decoded String
     */
    public static String decodeURIComponent(String s)
    {
        if (s == null)
        {
            return null;
        }

        String result = null;

        try
        {
            result = URLDecoder.decode(s, "UTF-8");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
        }

        return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The String to be encoded
     * @return the encoded String
     */
    public static String encodeURIComponent(String s)
    {
        String result = null;

        try
        {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
        }

        return result;
    }
}
