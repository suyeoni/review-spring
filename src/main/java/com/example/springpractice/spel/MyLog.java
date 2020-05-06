/*
 * Copyright (c) 2017 LINE Corporation. All rights reserved.
 * LINE Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.springpractice.spel;

import java.util.Map;

public class MyLog {
    private Long created;
    private String field1;
    private String field2;
    private String field3;
    private Map<String, Object> custom;

    public MyLog(Long created, String field1, String field2, String field3,
                 Map<String, Object> custom) {
        this.created = created;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.custom = custom;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public Map<String, Object> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, Object> custom) {
        this.custom = custom;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }
}
