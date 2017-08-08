package com.huntkey.rx.sceo.monitor.commom.model;

/**
 * Created by linziy on 2017/7/28.
 */
public class SortParam {
    /////////////////////////////////////////////////////////
    public final static String ATTR = "attr";
    public final static String SORT = "sort";
    /////////////////////////////////////////////////////////
    //排序字段
    private String attr;
    //排序 asc 升序 desc 降序
    private String sort;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }
    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort ;
    }

    @Override
    public String toString() {
        return "${" +
                "'attr':'" + attr + '\'' +
                ",'sort':'" + sort + '\'' +
                '}';
    }

}
