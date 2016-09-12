package com.catena.web.interceptor;

import com.catena.core.CatenaContext;
import com.catena.mock.MockRuntimeException;
import com.catena.mock.core.ScanUrlAndDataContext;
import com.catena.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hx-pc on 16-9-7.
 */
public class MockUrlConditionalInterceptor extends MockUrlSortLimitInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockUrlConditionalInterceptor.class);
    private static final String URL_DATA_KEY = "conditional";
    private static final String LESS_THAN = "$lt";
    private static final String GREATER_THAN = "$gt";
    private static final String LESS_THAN_EQUAL = "$lte";
    private static final String GREATER_THAN_EQUAL = "$gte";
    private static final String LIKE = "$regx";

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @Autowired
    private CatenaContext catenaContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkScanMockData();
        StringBuilder apiKey = getApiKey(request);
        String data = scanUrlAndDataContext.getDataWithApi(apiKey.toString(), request.getMethod());
        if (!StringUtils.isEmpty(data)) {
            Map<String, List<LinkedHashMap>> map = JsonUtil.readValue(data.getBytes(), Map.class);
            request.setAttribute("data", toConditional(request, map));
            catenaContext.getNodeOperationRepository().get("returnData").startReturnDataWithObject(request, response);
        }
        return false;
    }

    protected StringBuilder getApiKey(HttpServletRequest request) {
        LOGGER.info("请求 {}, 来源 {} ", request.getRequestURI(), request.getRemoteHost());
        return new StringBuilder(request.getRequestURI());
    }

    protected Map<String, Object> toConditional(HttpServletRequest request, Map<String, List<LinkedHashMap>> map) {
        Map<String, List<LinkedHashMap>> result = new HashMap<>();
        List<LinkedHashMap> list = map.get(URL_DATA_KEY);
        Stream<LinkedHashMap> stream = list.stream();
        for (Map.Entry<String, String[]> e : (request.getParameterMap()).entrySet()) {
            if (!e.getKey().equalsIgnoreCase("sort") && !e.getKey().equalsIgnoreCase("index") && !e.getKey().equalsIgnoreCase("size")) {
                for (String str : e.getValue()) {
                    stream = filter(e.getKey(), str, stream);
                }
            }
        }
        result.put(URL_DATA_KEY, stream.collect(Collectors.toList()));
        return toSortLimit(request, result);
    }

    protected Stream<LinkedHashMap> filter(String key, String value, Stream<LinkedHashMap> stream) {
        if (value.contains(LESS_THAN) || value.contains(GREATER_THAN) || value.contains(LESS_THAN_EQUAL) || value.contains(GREATER_THAN_EQUAL)) {
            return stream.filter(linkedHashMap -> toLtGtFilter(key, value, linkedHashMap));
        } else {
            if (value.contains(LIKE)) {
                return stream.filter(linkedHashMap -> ((String) linkedHashMap.get(key)).contains(value.substring(value.lastIndexOf(LIKE) + 4, value.length())));
            }
            return stream.filter(linkedHashMap -> linkedHashMap.get(key).equals(value));
        }
    }

    private boolean toLtGtFilter(String key, String value, LinkedHashMap linkedHashMap) {
        String dateFormatStr = "yyyy-MM-dd hh:mm:ss";
        try {
            if (value.contains(LESS_THAN_EQUAL)) {
                return new SimpleDateFormat(dateFormatStr).parse((String) linkedHashMap.get(key)).getTime() <= new SimpleDateFormat(dateFormatStr).parse(value.substring(value.lastIndexOf(LESS_THAN_EQUAL) + 4, value.length())).getTime();
            } else if (value.contains(GREATER_THAN_EQUAL)) {
                return new SimpleDateFormat(dateFormatStr).parse((String) linkedHashMap.get(key)).getTime() >= new SimpleDateFormat(dateFormatStr).parse(value.substring(value.lastIndexOf(GREATER_THAN_EQUAL) + 4, value.length())).getTime();
            } else if (value.contains(LESS_THAN)) {
                return new SimpleDateFormat(dateFormatStr).parse((String) linkedHashMap.get(key)).getTime() < new SimpleDateFormat(dateFormatStr).parse(value.substring(value.lastIndexOf(LESS_THAN) + 3, value.length())).getTime();
            } else if (value.contains(GREATER_THAN)) {
                return new SimpleDateFormat(dateFormatStr).parse((String) linkedHashMap.get(key)).getTime() > new SimpleDateFormat(dateFormatStr).parse(value.substring(value.lastIndexOf(GREATER_THAN) + 3, value.length())).getTime();
            }
        } catch (Exception e4) {
            try {
                if (value.contains(LESS_THAN_EQUAL)) {
                    return (Integer) linkedHashMap.get(key) <= Integer.valueOf(value.substring(value.lastIndexOf(LESS_THAN_EQUAL) + 4, value.length()));
                } else if (value.contains(GREATER_THAN_EQUAL)) {
                    return (Integer) linkedHashMap.get(key) >= Integer.valueOf(value.substring(value.lastIndexOf(GREATER_THAN_EQUAL) + 4, value.length()));
                } else if (value.contains(LESS_THAN)) {
                    return (Integer) linkedHashMap.get(key) < Integer.valueOf(value.substring(value.lastIndexOf(LESS_THAN) + 3, value.length()));
                } else if (value.contains(GREATER_THAN)) {
                    return (Integer) linkedHashMap.get(key) > Integer.valueOf(value.substring(value.lastIndexOf(GREATER_THAN) + 3, value.length()));
                }
            } catch (Exception e) {
                try {
                    if (value.contains(LESS_THAN_EQUAL)) {
                        return (Long) linkedHashMap.get(key) <= Long.valueOf(value.substring(value.lastIndexOf(LESS_THAN_EQUAL) + 4, value.length()));
                    } else if (value.contains(GREATER_THAN_EQUAL)) {
                        return (Long) linkedHashMap.get(key) >= Long.valueOf(value.substring(value.lastIndexOf(GREATER_THAN_EQUAL) + 4, value.length()));
                    } else if (value.contains(LESS_THAN)) {
                        return (Long) linkedHashMap.get(key) < Long.valueOf(value.substring(value.lastIndexOf(LESS_THAN) + 3, value.length()));
                    } else if (value.contains(GREATER_THAN)) {
                        return (Long) linkedHashMap.get(key) > Long.valueOf(value.substring(value.lastIndexOf(GREATER_THAN) + 3, value.length()));
                    }
                } catch (Exception e1) {
                    try {
                        if (value.contains(LESS_THAN_EQUAL)) {
                            return (Double) linkedHashMap.get(key) <= Double.valueOf(value.substring(value.lastIndexOf(LESS_THAN_EQUAL) + 4, value.length()));
                        } else if (value.contains(GREATER_THAN_EQUAL)) {
                            return (Double) linkedHashMap.get(key) >= Double.valueOf(value.substring(value.lastIndexOf(GREATER_THAN_EQUAL) + 4, value.length()));
                        } else if (value.contains(LESS_THAN)) {
                            return (Double) linkedHashMap.get(key) < Double.valueOf(value.substring(value.lastIndexOf(LESS_THAN) + 3, value.length()));
                        } else if (value.contains(GREATER_THAN)) {
                            return (Double) linkedHashMap.get(key) > Double.valueOf(value.substring(value.lastIndexOf(GREATER_THAN) + 3, value.length()));
                        }
                    } catch (Exception e2) {
                        try {
                            if (value.contains(LESS_THAN_EQUAL)) {
                                return (Float) linkedHashMap.get(key) <= Float.valueOf(value.substring(value.lastIndexOf(LESS_THAN_EQUAL) + 4, value.length()));
                            } else if (value.contains(GREATER_THAN_EQUAL)) {
                                return (Float) linkedHashMap.get(key) >= Float.valueOf(value.substring(value.lastIndexOf(GREATER_THAN_EQUAL) + 4, value.length()));
                            } else if (value.contains(LESS_THAN)) {
                                return (Float) linkedHashMap.get(key) < Float.valueOf(value.substring(value.lastIndexOf(LESS_THAN) + 3, value.length()));
                            } else if (value.contains(GREATER_THAN)) {
                                return (Float) linkedHashMap.get(key) > Float.valueOf(value.substring(value.lastIndexOf(GREATER_THAN) + 3, value.length()));
                            }
                        } catch (Exception e3) {
                            throw new MockRuntimeException(403, "转换失败:{}" + e.getMessage());
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected String getUrlDataKey() {
        return URL_DATA_KEY;
    }
}
