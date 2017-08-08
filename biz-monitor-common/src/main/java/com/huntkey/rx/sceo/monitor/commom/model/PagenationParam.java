package com.huntkey.rx.sceo.monitor.commom.model;

/**
 * Created by linziy on 2017/7/28.
 */
public class PagenationParam {
    public final static String PAGE = "startPage";
    public final static String ROWS = "rows";

    private int startpage;
    private int rows;

    public int getStartpage() {
        return startpage;
    }

    public void setStartpage(int startpage) {
        this.startpage = startpage;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getRows() {
        return this.rows;
    }

    @Override
    public String toString() {
        return "${" +
                "'startPage':'" + String.valueOf(startpage) + '\'' +
                ",'rows':'" + String.valueOf(rows) + '\'' +
                '}';
    }
}
