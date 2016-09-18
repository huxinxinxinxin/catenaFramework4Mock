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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hx-pc on 16-9-6.
 */
public class MockUrlSortLimitInterceptor extends MockUrlInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockUrlSortLimitInterceptor.class);

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @Autowired
    private CatenaContext catenaContext;


    private static final String URL_DATA_KEY = "list";
    private static final String SORT_DESC = "desc";
    private static final String SORT_ASC = "asc";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkScanMockData();
        StringBuilder apiKey = getApiKey(request);
        String data = scanUrlAndDataContext.getDataWithApi(apiKey.toString(), request.getMethod());
        if (!StringUtils.isEmpty(data)) {
            Map<String, List<LinkedHashMap>> map = JsonUtil.readValue(data.getBytes(), Map.class);
            request.setAttribute("data", toSortLimit(request, map));
            catenaContext.getNodeOperationRepository().get("returnData").startReturnDataWithString(request, response);
        }
        return false;
    }

    protected StringBuilder getApiKey(HttpServletRequest request) {
        StringBuilder apiKey = new StringBuilder(request.getRequestURI());
        if (request.getParameterNames() != null && request.getParameterMap().size() > 0) {
            apiKey.append("?");
            for (Map.Entry<String, String[]> e : (request.getParameterMap()).entrySet()) {
                if (!e.getKey().equalsIgnoreCase("sort") && !e.getKey().equalsIgnoreCase("index") && !e.getKey().equalsIgnoreCase("size")) {
                    apiKey.append(e.getKey()).append("=").append(e.getValue()[0]).append("&");
                }
            }
            apiKey = new StringBuilder(apiKey.substring(0, apiKey.length() - 1));
        }
        LOGGER.info("请求 {}, 来源 {} ", getUrlAddress(request), request.getRemoteHost());
        return apiKey;
    }

    protected Map<String, Object> toSortLimit(HttpServletRequest request, Map<String, List<LinkedHashMap>> resultMap) {
        Map<String, Object> result = new HashMap<>();
        Map<String, List<LinkedHashMap>> resultData = new HashMap<>();
        List<LinkedHashMap> list = resultMap.get(getUrlDataKey());
        Stream<LinkedHashMap> stream = list.stream();
        String sort = request.getParameter("sort");
        String index = request.getParameter("index");
        String size = request.getParameter("size");
        if (Objects.nonNull(sort)) {
            if (!sort.contains(SORT_DESC) && !sort.contains(SORT_ASC)) {
                sort += "."+SORT_DESC;
            }
            String key = sort.substring(0, sort.lastIndexOf("."));
            String order = sort.substring(sort.lastIndexOf(".") + 1, sort.length());
            if (key.contains(".")) {
                stream = stream.sorted(new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return compareTo(o1, o2, key, order);
                    }
                });
            } else {
                Comparator<LinkedHashMap> comparator = getComparator(resultMap, key, order);
                if (Objects.nonNull(comparator)) {
                    stream = stream.sorted(comparator);
                }
            }
        }

        if (Objects.nonNull(index) && Objects.nonNull(size)) {
            if (Integer.valueOf(index) - 1 < 0) {
                index = "1";
            }
            if (Integer.valueOf(size) - 1 < 0) {
                size = "10";
            }
            stream = stream.skip((Long.parseLong(index) - 1) * Long.parseLong(size)).limit(Long.parseLong(size));
            result.put("totalPage", list.size() % Integer.valueOf(size) == 0 ? list.size() / Integer.valueOf(size) : list.size() / Integer.valueOf(size) + 1);
            result.put("totalSize", list.size());
            result.put("index", Long.parseLong(index));
            result.put("size", Long.parseLong(size));
        }
        resultData.put(getUrlDataKey(), stream.collect(Collectors.toList()));
        result.put("data", resultData);
        return result;
    }

    protected int compareTo(Object o1, Object o2, String key, String order) {
        Integer result;
        o1 = getObject(key, (LinkedHashMap) o1);

        o2 = getObject(key, (LinkedHashMap) o2);
        String dateFormatStr = "yyyy-MM-dd hh:mm:ss";
        if (o1 instanceof Integer) {
            if ((Integer) o1 - (Integer) o2 > 0) {
                result = 1;
            } else if ((Integer) o1 - (Integer) o2 < 0) {
                result = -1;
            } else {
                result = 0;
            }
        } else if (o1 instanceof Long) {
            if ((Long) o1 - (Long) o2 > 0) {
                result = 1;
            } else if ((Long) o1 - (Long) o2 < 0) {
                result = -1;
            } else {
                result = 0;
            }
        } else if (o1 instanceof String) {
            if (((String) o1).compareTo((String) o2) > 0) {
                result = 1;
            } else if (((String) o1).compareTo((String) o2) < 0) {
                result = -1;
            } else {
                result = 0;
            }
        } else if (o1 instanceof Float) {
            if ((Float) o1 - (Float) o2 > 0) {
                result = 1;
            } else if ((Float) o1 - (Float) o2 < 0) {
                result = -1;
            } else {
                result = 0;
            }
        } else if (o1 instanceof Double) {
            if ((Double) o1 - (Double) o2 > 0) {
                result = 1;
            } else if ((Double) o1 - (Double) o2 < 0) {
                result = -1;
            } else {
                result = 0;
            }
        } else if (o1 instanceof Date) {
            try {
                if (new SimpleDateFormat(dateFormatStr).parse((String) o1).getTime() - new SimpleDateFormat(dateFormatStr).parse((String) o2).getTime() > 0) {
                    result = 1;
                } else if (new SimpleDateFormat(dateFormatStr).parse((String) o1).getTime() - new SimpleDateFormat(dateFormatStr).parse((String) o2).getTime() < 0) {
                    result = -1;
                } else {
                    result = 0;
                }
            } catch (ParseException e) {
                throw new MockRuntimeException(500, "时间转换失败");
            }
        } else {
            throw new MockRuntimeException(499, "类型校验错误");
        }
        if (order.equalsIgnoreCase(SORT_DESC)) {
            result = ~result + 1;
        }
        return result;
    }

    protected Comparator<LinkedHashMap> getComparator(Map<String, List<LinkedHashMap>> resultMap, String key, String order) {
        Comparator<LinkedHashMap> comparator = null;
        Object o = resultMap.get(getUrlDataKey()).get(0).get(key);
        if (o instanceof Integer) {
            final Function<LinkedHashMap, Integer> by = p1 -> (Integer) p1.get(key);
            comparator = Comparator.comparing(by);
        }
        if (o instanceof Long) {
            final Function<LinkedHashMap, Long> by = p1 -> (Long) p1.get(key);
            comparator = Comparator.comparing(by);
        }
        if (o instanceof String) {
            final Function<LinkedHashMap, String> by = p1 -> (String) p1.get(key);
            comparator = Comparator.comparing(by);
        }
        if (o instanceof Float) {
            final Function<LinkedHashMap, Float> by = p1 -> (Float) p1.get(key);
            comparator = Comparator.comparing(by);
        }
        if (o instanceof Double) {
            final Function<LinkedHashMap, Double> by = p1 -> (Double) p1.get(key);
            comparator = Comparator.comparing(by);
        }
        if (o instanceof Date) {
            final Function<LinkedHashMap, Date> by = p1 -> (Date) p1.get(key);
            comparator = Comparator.comparing(by);
        }
        if (Objects.nonNull(comparator) && order.equalsIgnoreCase(SORT_DESC)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }


    protected Object getObject(String key, LinkedHashMap linkedHashMap) {
        Object o;
        String useKey;
        Integer useIndex = 0;
        if (key.contains(".")) {
            String[] str = key.split("\\.");
            o = linkedHashMap.get(str[0]);
            for (int i = 1; i < str.length; i++) {
                if (str[i].contains("[")) {
                    useKey = str[i].substring(0, str[i].lastIndexOf("["));
                    useIndex = Integer.valueOf(str[i].substring(str[i].lastIndexOf("[") + 1, str[i].length() - 1));
                } else {
                    useKey = str[i];
                }
                o = ((LinkedHashMap) o).get(useKey);
                if (!(o instanceof LinkedHashMap)) {
                    if (o instanceof List) {
                        o = ((List) o).get(useIndex);
                    }
                }
            }
        } else {
            o = linkedHashMap.get(key);
        }
        return o;
    }

    protected String getUrlDataKey() {
        return URL_DATA_KEY;
    }
}
