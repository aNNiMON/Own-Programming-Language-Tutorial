package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public final class http_http implements Function {
    
    private static final Value
            HEADER_KEY = new StringValue("header"),
            CHARSET_KEY = new StringValue("charset");

    @Override
    public Value execute(Value... args) {
        String url, method;
        switch (args.length) {
            case 1: // http(url)
                url = args[0].asString();
                return process(url, "GET");
                
            case 2: // http(url, method) || http(url, callback)
                url = args[0].asString();
                if (args[1].type() == Types.FUNCTION) {
                    return process(url, "GET", (FunctionValue) args[1]);
                }
                return process(url, args[1].asString());
                
            case 3: // http(url, method, params) || http(url, method, callback)
                url = args[0].asString();
                method = args[1].asString();
                if (args[2].type() == Types.FUNCTION) {
                    return process(url, method, (FunctionValue) args[2]);
                }
                return process(url, method, args[2], FunctionValue.EMPTY);
                
            case 4: // http(url, method, params, callback)
                if (args[3].type() != Types.FUNCTION) {
                    throw new TypeException("Fourth arg must be a function callback");
                }
                url = args[0].asString();
                method = args[1].asString();
                return process(url, method, args[2], (FunctionValue) args[3]);
                
            case 5: // http(url, method, params, headerParams, callback)
                if (args[3].type() != Types.MAP) {
                    throw new TypeException("Third arg must be a map");
                }
                if (args[4].type() != Types.FUNCTION) {
                    throw new TypeException("Fifth arg must be a function callback");
                }
                url = args[0].asString();
                method = args[1].asString();
                return process(url, method, args[2], (MapValue) args[3], (FunctionValue) args[4]);
                
            default:
                throw new ArgumentsMismatchException("Wrong number of arguments");
        }
    }
    
    private Value process(String url, String method) {
        return process(url, method, FunctionValue.EMPTY);
    }
    
    private Value process(String url, String method, FunctionValue function) {
        return process(url, method, MapValue.EMPTY, function);
    }

    private Value process(String url, String method, Value params, FunctionValue function) {
        return process(url, method, params, MapValue.EMPTY, function);
    }
    
    private Value process(String url, String method, Value requestParams, MapValue options, FunctionValue function) {
        final Function callback = function.getValue();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpRequestBase httpMethod;
            switch (method.toUpperCase()) {
                case "POST":
                    httpMethod = new HttpPost(url);
                    break;
                case "PUT":
                    httpMethod = new HttpPut(url);
                    break;
                case "DELETE":
                    httpMethod = new HttpDelete(url);
                    break;
                case "PATCH":
                    httpMethod = new HttpPatch(url);
                    break;
                case "HEAD":
                    httpMethod = new HttpHead(url);
                    break;
                case "OPTIONS":
                    httpMethod = new HttpOptions(url);
                    break;
                case "TRACE":
                    httpMethod = new HttpTrace(url);
                    break;
                case "GET":
                default:
                    httpMethod = new HttpGet(url);
                    break;
            }
            
            if (options.containsKey(HEADER_KEY)) {
                applyHeaderParams((MapValue) options.get(HEADER_KEY), httpMethod);
            }
            
            if (httpMethod instanceof HttpEntityEnclosingRequestBase) {
                final HttpEntityEnclosingRequestBase heerb = (HttpEntityEnclosingRequestBase) httpMethod;
                if (requestParams.type() == Types.MAP) {
                    applyMapRequestParams(heerb, (MapValue) requestParams, options);
                } else {
                    applyStringRequestParams(heerb, requestParams, options);
                }
            }
            
            final HttpResponse httpResponse = httpClient.execute(httpMethod);
            final String response = new BasicResponseHandler().handleResponse(httpResponse);
            callback.execute(new StringValue(response));
            return NumberValue.fromBoolean(true);
        } catch (IOException ex) {
            return NumberValue.fromBoolean(false);
        }
    }
    
    private void applyHeaderParams(MapValue headerParams, HttpRequestBase httpMethod) {
        for (Map.Entry<Value, Value> p : headerParams) {
            httpMethod.addHeader(p.getKey().asString(), p.getValue().asString());
        }
    }
    
    private void applyMapRequestParams(HttpEntityEnclosingRequestBase h, MapValue params, MapValue options)
            throws UnsupportedEncodingException {
        final List<NameValuePair> entityParams = new ArrayList<>(params.size());
        for (Map.Entry<Value, Value> param : params) {
            final String name = param.getKey().asString();
            final String value = param.getValue().asString();
            entityParams.add(new BasicNameValuePair(name, value));
        }
        HttpEntity entity;
        if (options.containsKey(CHARSET_KEY)) {
            entity = new UrlEncodedFormEntity(entityParams, options.get(CHARSET_KEY).asString());
        } else {
            entity = new UrlEncodedFormEntity(entityParams);
        }
        h.setEntity(entity);
    }

    private void applyStringRequestParams(final HttpEntityEnclosingRequestBase heerb, Value requestParams, MapValue options) throws UnsupportedEncodingException, UnsupportedCharsetException {
        HttpEntity entity;
        if (options.containsKey(CHARSET_KEY)) {
            entity = new StringEntity(requestParams.asString(), options.get(CHARSET_KEY).asString());
        } else {
            entity = new StringEntity(requestParams.asString());
        }
        heerb.setEntity(entity);
    }

}