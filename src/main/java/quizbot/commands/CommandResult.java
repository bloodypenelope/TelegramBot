package quizbot.commands;

import java.util.List;

public class CommandResult {
    private final String result;
    private List<String> buttons;

    public CommandResult(String result) {
        this.result = result;
    }

    public CommandResult(String result, List<String> buttons) {
        this.result = result;
        this.buttons = buttons;
    }

    public String getResult() {
        return result;
    }

    public List<String> getButtons() {
        return buttons;
    }

    public boolean hasButtons() {
        return buttons != null;
    }
}