package xyz.scootaloo.bootshiro.support.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 快速的创建一个Map，并以构建者的形式放置键值对。
 * 使用Map，从此变得简单 :)
 * 在一般情况(没有指定泛型类型时)，第一次调用<code>put</code>方法的时候类型就确定了。
 * <pre class="code"> 默认使用方式，在需要使用map的时候：{@code
 *      MapPutter.put("name", "buck")
 *               .put("age", "17")
 *               .get();}
 * 在需要指定泛型的时候：{@code
 *     MapPutter.<String, Object>put("name", "buck")
 *              .put("age", 17)
 *              .get();}
 * </pre>
 *
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 15:16
 */
public class MapPutter {

    public static <K, V> Putter<K, V> put(K key, V value) {
        return new Putter<K, V>(key, value);
    }

    public static <K, V> Putter<K, V> set(Map<K, V> map) {
        return new Putter<>(map);
    }

    public static class Putter<K, V> {

        private final Map<K, V> map;

        public Putter(K key, V value) {
            map = new HashMap<>();
            map.put(key, value);
        }

        public Putter(Map<K, V> map) {
            this.map = map;
        }

        public Putter<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> get() {
            return this.map;
        }

    }

}
