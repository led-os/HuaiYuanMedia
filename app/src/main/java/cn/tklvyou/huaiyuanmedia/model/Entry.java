package cn.tklvyou.huaiyuanmedia.model;

import cn.tklvyou.huaiyuanmedia.base.BaseModel;

/**
 * 自定义Entry
 * *java.util.Map.Entry是interface，new Entry(...)不好用，其它的Entry也不好用
 *
 * @param <K> key
 * @param <V> value
 * @author Lemon
 * @use new Entry<K, V>(...)
 * @warn K, V都需要基本类型时不建议使用，判空麻烦，不如新建一个Model
 */
public class Entry<K, V> extends BaseModel {

    private static final long serialVersionUID = 1L;


    public K key;
    public V value;

    public Entry() {
        //default
    }

    public Entry(K key) {
        this(key, null);
    }

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }


    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }


}