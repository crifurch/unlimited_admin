package dev.crifurch.unlimitedadmin.modules.shop.data;

import java.util.ArrayList;
import java.util.List;

public class CommandTemplate {
    private final double cost;
    private final List<CommandValueTemplate> templateList = new ArrayList<>();
    private final Mode mode;


    public CommandTemplate(String command, double cost, Mode mode) {
        this.cost = cost;
        this.mode = mode;
        String[] split = command.split(" ");
        for (String s : split) {
            templateList.add(CommandValueTemplate.getTypeFromValue(s));
        }
    }

    public int getLength() {
        return templateList.size();
    }

    public double getCost() {
        return cost;
    }

    public Mode getMode() {
        return mode;
    }

    public CommandValueTemplate getTemplate(int index) {
        return templateList.get(index);
    }

    public List<CommandValueTemplate> getTemplateList() {
        return new ArrayList<>(templateList);
    }

    public enum Mode {
        MONEY,
        DONATE;

        //get mode from string and DONATE as default
        public static Mode getMode(String mode) {
            if (mode.equalsIgnoreCase("money")) {
                return MONEY;
            } else if (mode.equalsIgnoreCase("donate")) {
                return DONATE;
            } else {
                return DONATE;
            }
        }
    }
}
