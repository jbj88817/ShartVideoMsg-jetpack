package us.bojie.libnetwork;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.IntDef;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class Request<T, R> {
    private static final String TAG = "Request";
    protected String mUrl;
    private HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();

    public static final int CACHE_ONLY = 1;
    // First cache, and at same time visit network, if success, cache in local
    public static final int CACHE_FIRST = 2;

    public static final int NET_ONLY = 3;
    // first visit network, then cache to local
    public static final int NET_CACHE = 4;
    private String cacheKey;
    private Type mType;
    private Class mClazz;

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    public @interface CacheStrategy {

    }

    public Request(String url) {
        // user/list
        mUrl = url;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addParams(String key, Object value) {
        try {
            Field field = value.getClass().getField("TYPE");
            Class clazz = (Class) field.get(null);
            if (clazz.isPrimitive()) {
                params.put(key, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R) this;
    }

    public void execute(final JsonCallBack<T> callBack) {
        getCall().enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApiResponse<T> response = new ApiResponse<>();
                response.message = e.getMessage();
                callBack.onError(response);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ApiResponse<T> apiResponse = parseResponse(response, callBack);
                if (!apiResponse.success) {
                    callBack.onError(apiResponse);
                } else {
                    callBack.onSuccess(apiResponse);
                }

            }
        });
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallBack<T> callBack) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().string();
            if (success) {
                if (callBack != null) {
                    ParameterizedType type = (ParameterizedType) callBack.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                } else if (mClazz != null) {
                    result.body = (T) convert.convert(content, mClazz);
                } else {
                    Log.e(TAG, "parseResponse: cannot deserialized");
                }
            } else {
                message = content;
            }
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
        }
        result.success = success;
        result.status = status;
        result.message = message;
        return result;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }

    public R responseType(Class clazz) {
        mClazz = clazz;
        return (R) this;
    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeader(builder);
        okhttp3.Request request = generateRequest(builder);
        return ApiService.okHttpClient.newCall(request);
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeader(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    public ApiResponse<T> execute() {
        try {
            Response response = getCall().execute();
            return parseResponse(response, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
