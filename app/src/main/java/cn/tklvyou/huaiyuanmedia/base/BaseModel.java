package cn.tklvyou.huaiyuanmedia.base;

import java.io.Serializable;

public class BaseModel implements Serializable {

    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}