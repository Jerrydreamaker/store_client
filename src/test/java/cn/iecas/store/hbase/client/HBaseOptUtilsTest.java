package cn.iecas.store.hbase.client;
import junit.framework.TestCase;

import java.io.IOException;


public class HBaseOptUtilsTest extends TestCase {
    public void testCreateTable() throws IOException {
        HBaseOptUtils.createTable("test","cf");
    }
    public void testSave() throws IOException {
        User user=new User(2,"wlj");
        HBaseOptUtils.save("test",user.getPut());
    }





}