package xyz.scootaloo.bootshiro.support.cli;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 16:40
 */
public class HttpClientShell extends Shell {

    private final Scanner scanner = new Scanner(System.in); // 默认输入方式: 键盘录入
    private final Map<String, Consumer<List<String>>> strategyMap; // 策略Map

    public HttpClientShell() throws Exception {
        this(null);
    }

    public HttpClientShell(List<String> initCmdList) {
        strategyMap = StrategyFactory.register();
        initCmd(initCmdList);
    }

    @Override
    protected void interpret(List<String> args) throws RuntimeException {
        if (args == null || args.size() == 0)
            return;
        String cmdName = args.get(0);
        args.remove(0);

        for (String cmd : EXIT_COMMANDS) {
            if (cmdName.equals(cmd))
                return;
        }

        if (strategyMap.containsKey(cmdName)) {
            strategyMap.get(cmdName).accept(args);
        } else {
            error("没有这个命令" + cmdName);
        }
    }

    @Override
    protected void prompt() {
        System.out.print(ConsoleColor.GRAY + "bootshiro> ");
    }

    @Override
    protected void welcome() {
        System.out.println("bootshiro starting ... \nenter commands");
    }

    @Override
    protected String getInput() {
        return scanner.nextLine();
    }

}
