package cn.iecas.store.hbase.client;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

public class User implements HBaseModel {
    private Integer id;
    private String name;

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getRowkey() {
        return id+"";
    }

    @Override
    public Put getPut() {
        Put put=new Put(getRowkey().getBytes());
        put.add("cf".getBytes(),"id".getBytes(),BytesUtils.toBytes(id));
        put.add("cf".getBytes(),"name".getBytes(),BytesUtils.toBytes(name));
        return put;
    }

    @Override
    public User  getObject(Result rs) {
        id=HBaseResultUtils.getColumnValue(rs,"cf","name",Integer.class);
        name=HBaseResultUtils.getColumnValue(rs,"cf","name",String.class);
        return this;
    }
}
