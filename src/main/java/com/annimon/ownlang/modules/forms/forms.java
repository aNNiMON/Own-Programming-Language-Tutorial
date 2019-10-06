package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

/**
 *
 * @author aNNiMON
 */
public final class forms implements Module {

    public static void initConstants() {
        // JFrame constants
        Variables.define("DISPOSE_ON_CLOSE", NumberValue.of(JFrame.DISPOSE_ON_CLOSE));
        Variables.define("DO_NOTHING_ON_CLOSE", NumberValue.of(JFrame.DO_NOTHING_ON_CLOSE));
        Variables.define("EXIT_ON_CLOSE", NumberValue.of(JFrame.EXIT_ON_CLOSE));
        Variables.define("HIDE_ON_CLOSE", NumberValue.of(JFrame.HIDE_ON_CLOSE));

        // SwinfConstants
        final MapValue swing = new MapValue(20);
        swing.set("BOTTOM", NumberValue.of(SwingConstants.BOTTOM));
        swing.set("CENTER", NumberValue.of(SwingConstants.CENTER));
        swing.set("EAST", NumberValue.of(SwingConstants.EAST));
        swing.set("HORIZONTAL", NumberValue.of(SwingConstants.HORIZONTAL));
        swing.set("LEADING", NumberValue.of(SwingConstants.LEADING));
        swing.set("LEFT", NumberValue.of(SwingConstants.LEFT));
        swing.set("NEXT", NumberValue.of(SwingConstants.NEXT));
        swing.set("NORTH", NumberValue.of(SwingConstants.NORTH));
        swing.set("NORTH_EAST", NumberValue.of(SwingConstants.NORTH_EAST));
        swing.set("NORTH_WEST", NumberValue.of(SwingConstants.NORTH_WEST));
        swing.set("PREVIOUS", NumberValue.of(SwingConstants.PREVIOUS));
        swing.set("RIGHT", NumberValue.of(SwingConstants.RIGHT));
        swing.set("SOUTH", NumberValue.of(SwingConstants.SOUTH));
        swing.set("SOUTH_EAST", NumberValue.of(SwingConstants.SOUTH_EAST));
        swing.set("SOUTH_WEST", NumberValue.of(SwingConstants.SOUTH_WEST));
        swing.set("TOP", NumberValue.of(SwingConstants.TOP));
        swing.set("TRAILING", NumberValue.of(SwingConstants.TRAILING));
        swing.set("VERTICAL", NumberValue.of(SwingConstants.VERTICAL));
        swing.set("WEST", NumberValue.of(SwingConstants.WEST));
        Variables.define("SwingConstants", swing);

        // LayoutManagers constants
        final MapValue border = new MapValue(13);
        border.set("AFTER_LAST_LINE", new StringValue(BorderLayout.AFTER_LAST_LINE));
        border.set("AFTER_LINE_ENDS", new StringValue(BorderLayout.AFTER_LINE_ENDS));
        border.set("BEFORE_FIRST_LINE", new StringValue(BorderLayout.BEFORE_FIRST_LINE));
        border.set("BEFORE_LINE_BEGINS", new StringValue(BorderLayout.BEFORE_LINE_BEGINS));
        border.set("CENTER", new StringValue(BorderLayout.CENTER));
        border.set("EAST", new StringValue(BorderLayout.EAST));
        border.set("LINE_END", new StringValue(BorderLayout.LINE_END));
        border.set("LINE_START", new StringValue(BorderLayout.LINE_START));
        border.set("NORTH", new StringValue(BorderLayout.NORTH));
        border.set("PAGE_END", new StringValue(BorderLayout.PAGE_END));
        border.set("PAGE_START", new StringValue(BorderLayout.PAGE_START));
        border.set("SOUTH", new StringValue(BorderLayout.SOUTH));
        border.set("WEST", new StringValue(BorderLayout.WEST));
        Variables.define("BorderLayout", border);

        final MapValue box = new MapValue(4);
        box.set("LINE_AXIS", NumberValue.of(BoxLayout.LINE_AXIS));
        box.set("PAGE_AXIS", NumberValue.of(BoxLayout.PAGE_AXIS));
        box.set("X_AXIS", NumberValue.of(BoxLayout.X_AXIS));
        box.set("Y_AXIS", NumberValue.of(BoxLayout.Y_AXIS));
        Variables.define("BoxLayout", box);
    }

    @Override
    public void init() {
        initConstants();
        // Components
        Functions.set("newButton", Components::newButton);
        Functions.set("newLabel", Components::newLabel);
        Functions.set("newPanel", Components::newPanel);
        Functions.set("newProgressBar", Components::newProgressBar);
        Functions.set("newTextField", Components::newTextField);
        Functions.set("newWindow", Components::newWindow);

        // LayoutManagers
        Functions.set("borderLayout", LayoutManagers::borderLayout);
        Functions.set("boxLayout", LayoutManagers::boxLayout);
        Functions.set("cardLayout", LayoutManagers::cardLayout);
        Functions.set("gridLayout", LayoutManagers::gridLayout);
        Functions.set("flowLayout", LayoutManagers::flowLayout);
    }
}
