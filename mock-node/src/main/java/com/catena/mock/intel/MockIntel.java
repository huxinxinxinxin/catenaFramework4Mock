package com.catena.mock.intel;

import com.catena.core.annotation.CatenaNodeClass;
import com.catena.core.annotation.CatenaNodeMethod;
import com.catena.core.annotation.CatenaType;
import com.catena.mock.node.MockManageNode;
import com.catena.mock.param.MockBaseParam;

/**
 * Created by hx-pc on 16-5-20.
 */
@CatenaNodeClass (type = CatenaType.INTEL)
public interface MockIntel {

    @CatenaNodeMethod (key = {"addMock"},
            methods = {"addMock,writerMockFile,setScanResetToTrue"},
            nodes = {MockManageNode.class},
            paramClass = MockBaseParam.class
    )
    void addMock();

    @CatenaNodeMethod (key = {"getMock"},
            methods = {"getMockList"},
            nodes = {MockManageNode.class},
            paramClass = MockBaseParam.class
    )
    void getMock();


    @CatenaNodeMethod (key = {"returnData"},
            methods = {"returnData"},
            nodes = {MockManageNode.class},
            paramClass = MockBaseParam.class
    )
    void returnData();
}
