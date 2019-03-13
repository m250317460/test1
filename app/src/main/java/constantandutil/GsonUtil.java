package constantandutil;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangJiang on 2017/9/18.
 * Gson的封装
 */
public class GsonUtil {

    private static final Gson GSON = new Gson();

    public static <T> T fromJson(String json, Class<? extends T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> parseArray(String json, Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static String toJson(Object object, Type type) {
        return GSON.toJson(object, type);
    }

}
