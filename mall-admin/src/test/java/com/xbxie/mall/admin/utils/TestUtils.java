package com.xbxie.mall.admin.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * created by xbxie on 2024/4/21
 */
@Component
public class TestUtils {
    @Autowired
    private MockMvc mockMvc;

    public ResultActions basePerform(String url, Object payload) {
        MockHttpServletRequestBuilder request = post(url)
                .content(JSON.toJSONString(payload))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        ResultActions actions;
        try {
            actions = mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return actions;
    }

    public ResultActions simplePerform(String url, Object payload, int code, String message) {
        ResultActions actions;

        try {
            actions = basePerform(url, payload)
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.code").value(code));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return actions;
    }

    public void assertFail(String url, Object payload, String message) {
        simplePerform(url, payload, 500, message);
    }

    public void assertFailBatch(String url, List<? extends Object> list, String message)  {
        for (Object item : list) {
            assertFail(url, item, message);
        }
    }


}
