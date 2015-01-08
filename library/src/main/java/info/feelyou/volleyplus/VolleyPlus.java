package info.feelyou.volleyplus;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * A Volley client. It works with Square's OkHttp library.<br />
 * It use a {@link java.util.Map Map} as http request headers.<br />
 * And use {@link com.squareup.okhttp.RequestBody RequestBody} as http request body.<br />
 * {@link com.squareup.okhttp.RequestBody RequestBody} manual:
 * <a href="https://github.com/square/okhttp/wiki/Recipes" target="_blank">Recipes</a>
 * <p/>
 * Created by RemexHuang on 12/15/14.
 */
public class VolleyPlus {

    public static RequestQueue newRequestQueue(Context context) {
        return Volley.newRequestQueue(context, new OkHttpStack());
    }

}
