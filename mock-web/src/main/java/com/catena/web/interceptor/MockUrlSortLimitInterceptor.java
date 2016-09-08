package com.catena.web.interceptor;

import com.catena.core.CatenaContext;
import com.catena.mock.MockRuntimeException;
import com.catena.mock.core.ScanUrlAndDataContext;
import com.catena.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hx-pc on 16-9-6.
 */
public class MockUrlSortLimitInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockUrlSortLimitInterceptor.class);

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @Autowired
    private CatenaContext catenaContext;


    private static final String URL_DATA_KEY = "list";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkScanMockData();
        StringBuilder apiKey = getApiKey(request);
        String data = scanUrlAndDataContext.getDataWithApi(apiKey.toString(), request.getMethod());
        if (!StringUtils.isEmpty(data)) {
            Map<String, List<LinkedHashMap>> map = JsonUtil.readValue(data.getBytes(), Map.class);
            request.setAttribute("data", toSortLimit(request, map));
            catenaContext.getNodeOperationRepository().get("returnData").startReturnDataWithObject(request, response);
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
        LOGGER.info("请求 {}", apiKey);
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
            if (sort.split("\\.").length <= 1) {
                throw new MockRuntimeException(403,"sort格式错误,filed.asc/filed.desc");
            }
            String key = sort.split("\\.")[0];
            String order = sort.split("\\.")[1];
            if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
                throw new MockRuntimeException(403,"sort格式错误,filed.asc/filed.desc");
            }
            Comparator<LinkedHashMap> comparator = getComparator(resultMap, key, order);
            if (Objects.nonNull(comparator)) {
                stream = stream.sorted(comparator);
            }
        }
        if (Objects.nonNull(index)) {
            stream = stream.skip(Long.parseLong(index));
        }
        if (Objects.nonNull(size)) {
            stream = stream.limit(Long.parseLong(size));
            result.put("totalPage", list.size() % Integer.valueOf(size) == 0 ? list.size() / Integer.valueOf(size) : list.size() / Integer.valueOf(size) + 1);
        }
        resultData.put(getUrlDataKey(), stream.collect(Collectors.toList()));
        result.put("totalSize", list.size());
        result.put("data", resultData);
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
        if (Objects.nonNull(comparator) && order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    protected void checkScanMockData() throws IOException {
        List<File> files = new CopyOnWriteArrayList<>();
        File rootDir = new File(ScanUrlAndDataContext.FILE_PATH);
        for (File file : rootDir.listFiles()) {
            validateLastModified(files, file);
        }
        if (!CollectionUtils.isEmpty(files)) {
            scanUrlAndDataContext.scanFile(files);
        }
    }

    protected void validateLastModified(List<File> files, File file) {
        Map<String, Long> fileLastModified = scanUrlAndDataContext.getFileLastModified();
        if (Objects.isNull(fileLastModified.get(file.getName()))) {
            files.add(file);
        } else {
            if (!fileLastModified.get(file.getName()).equals(file.lastModified())) {
                files.add(file);
            }
        }
    }

    protected String getUrlDataKey() {
        return URL_DATA_KEY;
    }
}
