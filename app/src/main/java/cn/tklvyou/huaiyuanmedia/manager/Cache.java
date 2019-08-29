package cn.tklvyou.huaiyuanmedia.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.tklvyou.huaiyuanmedia.utils.JSON;

/**磁盘缓存类
 * @author Lemon
 * @param <T> 缓存的数据类
 * @use new Cache<T>(context, clazz, path).xxxMethod(...);具体参考.CacheManager
 */
public class Cache<T> {
	public static final String TAG = "Cache";

	private Class<T> clazz;
	private SharedPreferences sp;
	public Cache(Class<T> clazz, Context context, String name) {
		this(clazz, context.getSharedPreferences(name, Context.MODE_PRIVATE));
	}
	public Cache(Class<T> clazz, SharedPreferences sp) {
		this.clazz = clazz;
		this.sp = sp;
	}



	/**获取列表大小
	 * @return
	 */
	public int getSize() {
		Map<String, ?> map = sp.getAll();
		return map == null ? 0 : map.size();
	}


	/**保存
	 * @param map
	 */
	public void saveList(Map<String, T> map) {
		if (map == null) {
			Log.e(TAG, "saveList  map == null >> return;");
			return;
		}
		Set<String> keySet = map.keySet();
		if (keySet != null) {
			for (String id: keySet) {
				save(id, map.get(id));
			}
		}
	}

	/**保存
	 * @param key
	 * @param value
	 */
	public void save(String key, T value) {
		if (StringUtils.isEmpty(key) || value == null) {
			Log.e(TAG, "save StringUtil.isNotEmpty(key, true) == false || value == null >> return;");
			return;
		}

		sp.edit().remove(key).putString(key, JSON.toJSONString(value)).commit();
	}

	/**判断是否已存
	 * @param key
	 * @return
	 */
	public boolean isContain(String key) {
		if (StringUtils.isEmpty(key)) {
			Log.e(TAG, "isContain StringUtil.isNotEmpty(key, true) == false >> return false;");
			return false;
		}

		return sp.contains(key);
	}

	/**获取
	 * @param key
	 * @return
	 */
	public T get(String key) {
		if (StringUtils.isEmpty(key)) {
			Log.e(TAG, "get (sp == null" +
					" || StringUtil.isNotEmpty(key, true) == false >> return null; ");
			return null;
		}

		return JSON.parseObject(sp.getString(key, null), clazz);
	}


	/**ROOT
	 * 获取列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getMap() {
		try {
			return (Map<String, String>) sp.getAll();
		} catch (Exception e) {
			Log.e(TAG, "getMap try { return (Map<String, String>) sp.getAll();" +
					"}catch(Exception e) {\n " + e.getMessage());
		}
		return null;
	}

	/**ROOT
	 * 获取列表
	 * @return
	 */
	public Set<String> getKeySet() {
		Map<String, String> map = getMap();
		return map == null ? null : map.keySet();
	}

	/**ROOT
	 * 获取列表
	 * @param start < 0 ? all : [start, end] 
	 * @param end
	 * @return
	 */
	public List<T> getValueList(int start, int end) {
		List<T> list = getAllValueList();
		return start < 0 || start > end || list == null || end >= list.size() ? list : list.subList(start, end);
	}
	/**ROOT
	 * 获取列表,顺序由keyList指定
	 * @param keyList
	 * @return
	 */
	public List<T> getValueList(List<String> keyList) {
		if (keyList != null) {
			List<T> list = new ArrayList<T>();
			T data;
			for (String key : keyList) {
				data = get(key);
				if (data != null) {
					list.add(data);
				}
			}
			return list;
		}
		return null;
	}
	/**ROOT
	 * 获取列表
	 * @return
	 */
	public List<T> getAllValueList() {
		Map<String, String> map = getMap();
		if (map != null) {
			List<T> list = new ArrayList<T>();
			T data;
			for (String value : map.values()) {
				data = JSON.parseObject(value, clazz);
				if (data != null) {
					list.add(data);
				}
			}
			return list;
		}
		return null;
	}

	/**删除
	 * @param key
	 */
	public void remove(String key) {
		if (StringUtils.isEmpty(key)) {
			Log.e(TAG, "deleteGroup  context == null " +
					" || StringUtil.isNotEmpty(groupName, true) == fal >> return;");
			return;
		}

		sp.edit().remove(key).commit();
	}


}