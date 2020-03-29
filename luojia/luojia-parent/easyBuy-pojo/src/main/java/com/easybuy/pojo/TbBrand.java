package com.easybuy.pojo;

import java.io.Serializable;

/**
 * 品牌
 * @author Romantic
 * @CreateDate 2020年3月29日
 * @Description
 */
public class TbBrand implements Serializable {
    private Long id;

    private String name;

    private String firstChar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar == null ? null : firstChar.trim();
    }
}