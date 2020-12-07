package xyz.scootaloo.bootshiro.support;

import xyz.scootaloo.bootshiro.utils.StringUtils;

import java.util.function.Consumer;

/**
 * 发现一些地方写了很多if else的代码，很不美观。
 * 很多情况下都是这样的代码: 假如某条件成立，则对某对象执行一些操作。
 * <p>所以这里为这些操作提供一些快捷的处理方式，尤其是对于处理某些表单，
 * 判断一个字段是否为空，使用这个对象可以少写很多if else代码。</p>
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 20:00
 */
public class VarInspector<T> {

    private final T obj;

    public VarInspector(T object) {
        this.obj = object;
    }

    public void check(boolean flag, Consumer<T> func) {
        if (flag)
            func.accept(obj);
    }

    /**
     * 为处理字符串对象提供的快捷操作，例如处理一个表单对象的时候经常需要检查一个字段是否符合某条件。
     * 这里，假如prop为空字符串，则func函数执行。
     * @param prop 一个字符串对象
     * @param func 处理obj
     */
    public void ifNotEmpty(CharSequence prop, Consumer<T> func) {
        if (StringUtils.isEmpty(prop)) {
            func.accept(obj);
        }
    }

    /**
     * 假如prop这个属性不为空，则来一个方法接收这个prop,
     * 第二个参数通常是方法引用，这是if else的简化写法。
     * <pre>
     *     checker.ifNotEmptyThenSet(user.getName(), user::setName);
     *     checker.ifNotEmptyThenSet(map.get("name"), user::setName);
     * </pre>
     * @param prop 一个字符序列
     * @param func 接收字符序列的方法，或者是接口实现
     */
    public void ifNotEmptyThenSet(CharSequence prop, Consumer<String> func) {
        if (StringUtils.isEmpty(prop)) {
            func.accept((String) prop);
        }
    }

}
