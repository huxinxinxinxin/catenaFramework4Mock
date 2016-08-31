package com.catena.mock.node;

import com.catena.core.node.CatenaNode;
import com.catena.mock.MockRuntimeException;
import com.catena.mock.core.HttpRequestMethod;
import com.catena.mock.core.ScanUrlAndDataContext;
import com.catena.mock.param.MockBaseParam;
import com.catena.response.MockReturnResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by hx-pc on 16-5-20.
 */
public class MockManageNode extends CatenaNode {

    private static final String API_STR = "apiStr";
    private static final String DATA_STR = "dataStr";
    private static final String REQUEST_ERROR = "请求方式错误";

    public void setScanResetToTrue() {
        getBean(ScanUrlAndDataContext.class).setScanSwitch(true);
    }

    public void addMock(MockBaseParam mockBaseParam) {
        String apiStr = "\n" + getBean(ScanUrlAndDataContext.class).getEnvironmentMap().get(ScanUrlAndDataContext.API_KEY) + "." + mockBaseParam.getKey() + "=" + mockBaseParam.getApiValue() + "\n";
        String dataStr = getBean(ScanUrlAndDataContext.class).getEnvironmentMap().get(getDataKey(mockBaseParam.getHttpRequestMethod())) + "." + mockBaseParam.getKey() + "=" + mockBaseParam.getDataValue() + "\n";
        mockBaseParam.getOtherParam().put(API_STR, apiStr.getBytes());
        mockBaseParam.getOtherParam().put(DATA_STR, dataStr.getBytes());
    }

    public void writerMockFile(MockBaseParam mockBaseParam) throws IOException {
        File file = new File(ScanUrlAndDataContext.FILE_PATH + ScanUrlAndDataContext.FILE_NAME + ".properties");
        if (!file.exists()) {
            if(!file.createNewFile()) {
                throw new MockRuntimeException("mockConfig.properties 创建失败");
            }
        }
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write((byte[]) mockBaseParam.getOtherParam().get(API_STR));
        fos.write(clearBlank((byte[]) mockBaseParam.getOtherParam().get(DATA_STR)));
        fos.close();
    }

    private String getDataKey(HttpRequestMethod httpRequestMethod) {
        if (httpRequestMethod == HttpRequestMethod.GET) {
            return ScanUrlAndDataContext.DATA_GET_KEY;
        } else if (httpRequestMethod == HttpRequestMethod.POST) {
            return ScanUrlAndDataContext.DATA_POST_KEY;
        } else if (httpRequestMethod == HttpRequestMethod.PUT) {
            return ScanUrlAndDataContext.DATA_PUT_KEY;
        } else if (httpRequestMethod == HttpRequestMethod.DELETE) {
            return ScanUrlAndDataContext.DATA_DELETE_KEY;
        } else {
            throw new MockRuntimeException(REQUEST_ERROR);
        }
    }

    public void getMockList(MockBaseParam mockBaseParam) {
        mockBaseParam.setHttpRequestMethod(getHttpRequestMethod(mockBaseParam.acceptServletRequest().getMethod()));
        Map<String, String> map = getResourceData(mockBaseParam.getHttpRequestMethod());
        List<MockReturnResponse> list = new CopyOnWriteArrayList<>();
        map.entrySet().forEach(stringStringEntry -> list.add(MockReturnResponse.build(stringStringEntry.getKey(), stringStringEntry.getValue())));
        mockBaseParam.setData(list);
    }

    private HttpRequestMethod getHttpRequestMethod(String method) {
        if (method.equalsIgnoreCase(HttpRequestMethod.GET.name())) {
            return HttpRequestMethod.GET;
        } else if (method.equalsIgnoreCase(HttpRequestMethod.POST.name())) {
            return HttpRequestMethod.POST;
        } else if (method.equalsIgnoreCase(HttpRequestMethod.PUT.name())) {
            return HttpRequestMethod.PUT;
        } else if (method.equalsIgnoreCase(HttpRequestMethod.DELETE.name())) {
            return HttpRequestMethod.DELETE;
        } else {
            throw new MockRuntimeException(REQUEST_ERROR);
        }
    }


    private Map<String, String> getResourceData(HttpRequestMethod httpRequestMethod) {
        if (httpRequestMethod == HttpRequestMethod.GET) {
            return getBean(ScanUrlAndDataContext.class).getResourceDataGetMap();
        } else if (httpRequestMethod == HttpRequestMethod.POST) {
            return getBean(ScanUrlAndDataContext.class).getResourceDataPostMap();
        } else if (httpRequestMethod == HttpRequestMethod.PUT) {
            return getBean(ScanUrlAndDataContext.class).getResourceDataPutMap();
        } else if (httpRequestMethod == HttpRequestMethod.DELETE) {
            return getBean(ScanUrlAndDataContext.class).getResourceDataDeleteMap();
        } else {
            throw new MockRuntimeException(REQUEST_ERROR);
        }
    }

    /**
     * 清除在字符串所有的空格
     */
    private byte[] clearBlank(byte[] resource) {
        StringBuilder returnStr = new StringBuilder();
        for (byte b : resource) {
            if (!((char) b == ' ')) {
                returnStr.append((char) b);
            }
        }
        return returnStr.toString().getBytes();
    }


    public void returnData(MockBaseParam mockBaseParam) {
        mockBaseParam.setData(mockBaseParam.acceptServletRequest().getAttribute("data"));
    }
}
