package util;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * 缓存工具类
 * @author Administrator
 *
 */
public class CacheUtil {

	private RedisTemplate<String, Object> redisTemplate;

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}  
	
	//添加对象
	public void addValue(String key,Object value){
		redisTemplate.boundValueOps(key).set(value);
	}
	
	//获取对象
	public Object getValue(String key){
		return redisTemplate.boundValueOps(key).get();
	}
	
	//删除对象
	public void removeValue(String key){
		redisTemplate.delete(key);
	}
	
	//向Set集合添加值
	public void addSetValue(String key,Object value){
		redisTemplate.boundSetOps(key).add(value);
	}
	
	//获取Set集合的值
	public Set getSetValue(String key){
		return redisTemplate.boundSetOps(key).members();
	}
	
	//删除Set集合中的某个值
	public void removeSetValue(String key,Object value){
		redisTemplate.boundSetOps(key).remove(value);
	}
	
	
	//向List集合添加值
	public void addListValue(String key,Object value){
		redisTemplate.boundListOps(key).leftPush(value);
	}
	
	//获取List集合
	public List getListValues(String key){
		return redisTemplate.boundListOps(key).range(0, -1);//-1代表所有
	}
	
	//获取List集合
	public Object getListValueByIndex(String key,long  index){
		return redisTemplate.boundListOps(key).index(index);
	}
	
	//删除List集合中的某个值
	public void removeListValue(String key,long index){
		redisTemplate.boundListOps(key).remove(index,null);
	}
	
	
	
}
