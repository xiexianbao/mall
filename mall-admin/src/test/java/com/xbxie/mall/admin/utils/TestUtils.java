package com.xbxie.mall.admin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * created by xbxie on 2024/4/21
 */
@Component
public class TestUtils {
    @Autowired
    private MockMvc mockMvc;

    public <T> T getResData(String url, Object payload, TypeReference<T> t) {
        String contentAsString = "";
        try {
            contentAsString = basePerformAssert(url, payload).andReturn().getResponse().getContentAsString(Charset.forName("utf8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return JSON.parseObject(contentAsString, t);
    }

    public R getResData(String url, Object payload) {
        String contentAsString = "";
        try {
            contentAsString = basePerformAssert(url, payload).andReturn().getResponse().getContentAsString(Charset.forName("utf8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if (StringUtils.isEmpty(contentAsString)) {
            contentAsString = "{code:-1000}";
        }
        return JSON.parseObject(contentAsString, new TypeReference<R>(){});
    }

    public MockHttpServletResponse getResponse(String url, Object payload) {
        return basePerformAssert(url, payload).andReturn().getResponse();
    }

    public ResultActions basePerformAssert(String url, Object payload) {
        MockHttpServletRequestBuilder request = post(url)
                .content(JSON.toJSONString(payload))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        ResultActions actions;
        try {
            actions = mockMvc.perform(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return actions;
    }

    public ResultActions simplePerformAssert(String url, Object payload, int code, String message) {
        ResultActions actions;

        try {
            actions = basePerformAssert(url, payload)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.code").value(code));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return actions;
    }

    public void assertSuccess(String url, Object payload, String message) {
        simplePerformAssert(url, payload, 0, message);
    }

    public void assertFail(String url, Object payload, String message) {
        simplePerformAssert(url, payload, 500, message);
    }

    public void assertFailBatch(String url, List<?> list, String message)  {
        for (Object item : list) {
            assertFail(url, item, message);
        }
    }

    public boolean deletePhysical(String tableName, Long id) {
        return SqlRunner.db().delete("delete from " + tableName + " where id = " + id);
    }

    public boolean deleteBatchPhysical(String tableName, List<Long> idList) {
        String idsStr = idList.stream().map(String::valueOf).collect(Collectors.joining(","));
        return SqlRunner.db().delete("delete from " + tableName + " where id in ( " + idsStr + " )");
    }
}
