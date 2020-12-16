package xyz.scootaloo.bootshiro.support.cli;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 16:32
 */
@FunctionalInterface
public interface Interpreter {

    Object interpret(String command) throws RuntimeException;

}
