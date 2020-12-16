package xyz.scootaloo.bootshiro.support.cli;

/**
 * idea颜色代码
 * 使用时
 * <code>System.out.println(ConsoleColor.RED + "信息")</code>
 * 就可以输出有颜色的字体
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 17:15
 */
public interface ConsoleColor {

    // 白
    String WHITE    = "\u001B[30m";

    // 红
    String RED      = "\u001B[31m";

    // 绿
    String GREEN    = "\u001B[32m";

    // 黄
    String YELLOW   = "\u001B[33m";

    // 蓝
    String BLUE     = "\u001B[34m";

    // 紫
    String PURPLE   = "\u001B[35m";

    // 青
    String CYANOGEN = "\u001B[36m";

    // 灰
    String GRAY     = "\u001B[37m";

}
