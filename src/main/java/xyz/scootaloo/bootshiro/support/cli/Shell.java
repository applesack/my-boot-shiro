package xyz.scootaloo.bootshiro.support.cli;

import xyz.scootaloo.bootshiro.utils.StringUtils;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 16:32
 */
public abstract class Shell implements Interpreter {

    protected static final String[] EXIT_COMMANDS = {"exit", "quit"};

    public Object interpret(String command) throws RuntimeException {
        interpret(StringUtils.splitBy(command, ' '));
        return null;
    }

    public final void run() {
        welcome();
        String command = "";
        while (!isExitCommand(command)) {
            prompt();
            command = getInput();
            try {
                interpret(command);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        exit();
    }

    protected void initCmd(List<String> cmdList) {
        if (cmdList == null)
            return;
        for (String cmd : cmdList) {
            interpret(cmd);
        }
    }

    protected void error(String msg) {
        System.out.print(ConsoleColor.RED + msg);
    }

    protected void welcome() {
        System.out.println("shell is running...");
    }

    protected void exit() {
        System.out.println("exit.");
    }

    protected void prompt() {
        System.out.print("shell> ");
    }

    protected boolean isExitCommand(String command) {
        command = command.trim();
        for (String cmd : EXIT_COMMANDS) {
            if (cmd.equals(command))
                return true;
        }
        return false;
    }

    protected abstract void interpret(List<String> args) throws RuntimeException;

    protected abstract String getInput();

}
