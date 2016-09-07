package com.catena.web.interceptor;

import com.catena.core.CatenaContext;
import com.catena.mock.core.ScanUrlAndDataContext;
import com.catena.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @Autowired
    private CatenaContext catenaContext;

    private static final String URL_DATA_KEY = "conditional";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkScanMockData();
        StringBuilder apiKey = new StringBuilder(request.getRequestURI());
        String data = scanUrlAndDataContext.getDataWithApi(apiKey.toString(), request.getMethod());
        if (!StringUtils.isEmpty(data)) {
            Map<String, List<LinkedHashMap>> map = JsonUtil.readValue(data.getBytes(), Map.class);
            request.setAttribute("data", toConditional(request, map));
            catenaContext.getNodeOperationRepository().get("returnData").startReturnDataWithObject(request, response);
        }
        return false;
    }

    protected Map<String, List<LinkedHashMap>> toConditional(HttpServletRequest request, Map<String, List<LinkedHashMap>> map) {
        Map<String, List<LinkedHashMap>> result = new HashMap<>();
        List<LinkedHashMap> list = map.get(URL_DATA_KEY);
        Stream<LinkedHashMap> stream = list.stream();
        for (Map.Entry<String, String[]> e : (request.getParameterMap()).entrySet()) {
            stream = filter(e.getKey(), e.getValue()[0], stream);
        }
        result.put(URL_DATA_KEY, stream.collect(Collectors.toList()));
        return toSortLimit(request, result);
    }

    protected Stream<LinkedHashMap> filter(String key, String value, Stream<LinkedHashMap> stream) {
        return stream.filter(linkedHashMap -> linkedHashMap.get(key).equals(value));
    }
}
