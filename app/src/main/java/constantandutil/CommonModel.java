package constantandutil;

/**
 * Created by dell on 2018/8/14.
 */

public class CommonModel {

    /**
     * code : 100
     * message : 登录失败，密码不匹配或账号未注册
     * data : {"name":"asd"}
     */

    public int code;
    public String message;
    public DataBean data;
    public String count;

    public static class DataBean {
        /**
         * name : asd  yeshi
         */
        public String path;
        public String name;
    }
}