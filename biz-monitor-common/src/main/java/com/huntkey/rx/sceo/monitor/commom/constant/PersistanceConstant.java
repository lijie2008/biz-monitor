package com.huntkey.rx.sceo.monitor.commom.constant;

/**
 * Created by zhanglb on 2017/6/28.
 */
public class PersistanceConstant {

    //定义json中属性字段key
    public static String PERSISTANCE_ATTR = "attr";

    //定义json中字段条件key
    public static String PERSISTANCE_OPERATOR = "operator";

    //定义json中属性value值的key
    public static String PERSISTANCE_VALUE = "value";

    //条件where连接，目前只考虑and关系
    public static String PERSISTANCE_AND = " AND ";

    //Id字段，默认Id是每个表主键，如果多条件查询或者删除，有Id
    public static String PERSISTANCE_ID = "id";

    //常量1
    public static int PERSISTANCE_ONE = 1;

    //常量4
    public static int PERSISTANCE_FOUR = 4;

    //定义常量tablename用户传给mapper.xml中用
    public static String TABLE_NAME = "tableName";

    //定义常量condition用户传给mapper.xml中用
    public static String CONDITION = "condition";

    //定义常量columns用户传给mapper.xml中用
    public static String COLUMNS = "columns";

    //定义常量setCondition用户传给mapper.xml中用
    public static String SET_CONDITION = "setCondition";

    //mysql表都又有个is_del字段，0：有效，1：删除
    public static String SET_DELETE_CONDITION = "is_del = '1'";

    public static String ORDER_BY_CONDITION = "orderByCondition";

    public static String CONDITIONS = "conditions";

    public static String EDMNAME = "edmName";

    public static String ID = "id";

    public static String PID = "pid";

    public static String ORDERBY = "orderby";

    public static String DATASET = "dataset";

    public static String PAGENATION = "pagenation";

    public static String STARTPAGE = "startPage";

    public static String ROWS = "rows";
    
    public static String EDMPCODE = "moni012";
    
    public static String MONITORTREEORDER = "monitortreeorder";
    
    public static String MTOR_MTOR005A = "monitortreeorder.mtor005";
    
    public static String MTOR_MTOR019B = "monitortreeorder.mtor019";
    
}
