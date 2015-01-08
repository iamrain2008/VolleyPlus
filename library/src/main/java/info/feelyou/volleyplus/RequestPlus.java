package info.feelyou.volleyplus;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import okio.Buffer;

/**
 * A request use OkHttp's {@link com.squareup.okhttp.RequestBody} and {@link java.util.Map} headers.
 * <p/>
 * Created by RemexHuang on 12/17/14.
 */
public abstract class RequestPlus<T> extends Request<T> {

    /**
     * Request headers
     */
    private Map<String, String> headers;

    /**
     * Request body
     */
    private RequestBody body;

    private FinishListener finishListener;

    public RequestPlus(int method, String url,
                       Response.ErrorListener errorListener,
                       FinishListener finishListener) {
        super(method, url, errorListener);
        this.finishListener = finishListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        int flag = 0;

        Map<String, String> map = getHeadersUseRequestBody();
        if (map != null && map.size() > 0)
            flag |= 1 << 1;

        if (headers != null && headers.size() > 0)
            flag |= 1;

        switch (flag) {
            case 0:
                //map and headers both are null
                return super.getHeaders();
            case 1:
                //map is null, headers not null
                return headers;
            case 2:
                //map not null, headers is null
                return map;
            case 3:
                //If both map and headers not null,use key and value in map replace headers
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    headers.put(entry.getKey(), entry.getValue());
                }
                return headers;
            default:
                return super.getHeaders();
        }

    }

    private Map<String, String> getHeadersUseRequestBody() {
        Map<String, String> map = null;

        if (body != null) {
            map = new HashMap<String, String>();
            MediaType contentType = body.contentType();
            if (contentType != null) {
                map.put("Content-Type", contentType.toString());
            }

            long contentLength = body.contentLength();
            if (contentLength != -1) {
                map.put("Content-Length", Long.toString(contentLength));
                map.remove("Transfer-Encoding");
            } else {
                map.put("Transfer-Encoding", "chunked");
                map.remove("Content-Length");
            }
        }

        return map;
    }

    /**
     * Replace existing headers.
     *
     * @param headers new headers
     */
    public void putHeaders(Map<String, String> headers) {
        if (headers == null) throw new IllegalArgumentException("headers == null");
        this.headers = headers;
    }

    /**
     * Add additive headers to existing headers.
     *
     * @param additiveHeaders additive headers
     */
    public void addHeaders(Map<String, String> additiveHeaders) {
        if (additiveHeaders == null) throw new IllegalArgumentException("additiveHeaders == null");

        if (headers == null) {
            headers = additiveHeaders;
        } else {
            for (Map.Entry<String, String> entry : additiveHeaders.entrySet()) {
                headers.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Add key,value to existing headers.
     *
     * @param key   key
     * @param value value
     */
    public void addHeader(String key, String value) {
        if (key == null) throw new IllegalArgumentException("name == null");
        if (value == null) throw new IllegalArgumentException("value == null");
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
    }

    public RequestBody getRequestBody() {
        return body;
    }

    /**
     * Set request body data use {@link com.squareup.okhttp.RequestBody}
     *
     * @param requestBody body
     */
    public void setRequestBody(RequestBody requestBody) {
        this.body = requestBody;
    }

    /**
     * Please use {@link #setRequestBody} set params.
     *
     * @return
     * @throws com.android.volley.AuthFailureError
     */
    @Override
    final protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }

    /**
     * Convert {@link com.squareup.okhttp.RequestBody} to byte[]
     *
     * @return http request body
     * @throws com.android.volley.AuthFailureError
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        return convertRequestBodyToByte(body);
    }

    /**
     * Convert {@link com.squareup.okhttp.RequestBody} to byte[]
     *
     * @param requestBody target RequestBody
     * @return byte[]
     */
    private byte[] convertRequestBodyToByte(RequestBody requestBody) {
        byte[] body = null;
        Buffer buffer = null;
        try {
            if (requestBody != null) {
                buffer = new Buffer();
                requestBody.writeTo(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (buffer != null) {
            body = buffer.readByteArray();
        }
        return body;
    }

    @Override
    protected void deliverResponse(T response) {
        VolleyLog.v("\n" + buildHeadersString() + "\n" + buildBodyString());
        onResponse(response);
        if (finishListener != null) {
            finishListener.onFinish();
        }
    }

    abstract protected void onResponse(T response);

    @Override
    public void deliverError(VolleyError error) {
        VolleyLog.e("\n" + buildHeadersString() + "\n" + buildBodyString());
        super.deliverError(error);
        if (finishListener != null) {
            finishListener.onFinish();
        }
    }

    public String buildHeadersString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Headers {");
        try {
            Map<String, String> headersMap = getHeaders();
            for (Map.Entry entry : headersMap.entrySet()) {
                stringBuilder.append("[");
                stringBuilder.append(entry.getKey());
                stringBuilder.append("='");
                stringBuilder.append(entry.getValue());
                stringBuilder.append("']");
            }
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public String buildBodyString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Body {");
        if (body != null) {
            stringBuilder.append("[contentType='");
            stringBuilder.append(body.contentType() != null ? body.contentType() : "");
            stringBuilder.append("'],");
            stringBuilder.append("[body='");
            try {
                stringBuilder.append(new String(convertRequestBodyToByte(body), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                stringBuilder.append("(body convert to string error)");
            }
            stringBuilder.append("']");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
