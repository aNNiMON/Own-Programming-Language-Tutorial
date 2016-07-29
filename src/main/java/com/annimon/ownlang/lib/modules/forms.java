package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.annotations.ConstantInitializer;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.forms.Components;
import com.annimon.ownlang.lib.modules.functions.forms.LayoutManagers;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

/**
 *
 * @author aNNiMON
 */
@ConstantInitializer
public final class forms implements Module {

    public static void initConstants() {
        // JFrame constants
        Variables.define("DISPOSE_ON_CLOSE", NumberValue.of(JFrame.DISPOSE_ON_CLOSE));
        Variables.define("DO_NOTHING_ON_CLOSE", NumberValue.of(JFrame.DO_NOTHING_ON_CLOSE));
        Variables.define("EXIT_ON_CLOSE", NumberValue.of(JFrame.EXIT_ON_CLOSE));
        Variables.define("HIDE_ON_CLOSE", NumberValue.of(JFrame.HIDE_ON_CLOSE));

        // SwinfConstants
        final MapValue swing = new MapValue(20);
        swing.set(new StringValue("BOTTOM"), NumberValue.of(SwingConstants.BOTTOM));
        swing.set(new StringValue("CENTER"), NumberValue.of(SwingConstants.CENTER));
        swing.set(new StringValue("EAST"), NumberValue.of(SwingConstants.EAST));
        swing.set(new StringValue("HORIZONTAL"), NumberValue.of(SwingConstants.HORIZONTAL));
        swing.set(new StringValue("LEADING"), NumberValue.of(SwingConstants.LEADING));
        swing.set(new StringValue("LEFT"), NumberValue.of(SwingConstants.LEFT));
        swing.set(new StringValue("NEXT"), NumberValue.of(SwingConstants.NEXT));
        swing.set(new StringValue("NORTH"), NumberValue.of(SwingConstants.NORTH));
        swing.set(new StringValue("NORTH_EAST"), NumberValue.of(SwingConstants.NORTH_EAST));
        swing.set(new StringValue("NORTH_WEST"), NumberValue.of(SwingConstants.NORTH_WEST));
        swing.set(new StringValue("PREVIOUS"), NumberValue.of(SwingConstants.PREVIOUS));
        swing.set(new StringValue("RIGHT"), NumberValue.of(SwingConstants.RIGHT));
        swing.set(new StringValue("SOUTH"), NumberValue.of(SwingConstants.SOUTH));
        swing.set(new StringValue("SOUTH_EAST"), NumberValue.of(SwingConstants.SOUTH_EAST));
        swing.set(new StringValue("SOUTH_WEST"), NumberValue.of(SwingConstants.SOUTH_WEST));
        swing.set(new StringValue("TOP"), NumberValue.of(SwingConstants.TOP));
        swing.set(new StringValue("TRAILING"), NumberValue.of(SwingConstants.TRAILING));
        swing.set(new StringValue("VERTICAL"), NumberValue.of(SwingConstants.VERTICAL));
        swing.set(new StringValue("WEST"), NumberValue.of(SwingConstants.WEST));
        Variables.define("SwingConstants", swing);

        // LayoutManagers constants
        final MapValue border = new MapValue(13);
        border.set(new StringValue("AFTER_LAST_LINE"), new StringValue(BorderLayout.AFTER_LAST_LINE));
        border.set(new StringValue("AFTER_LINE_ENDS"), new StringValue(BorderLayout.AFTER_LINE_ENDS));
        border.set(new StringValue("BEFORE_FIRST_LINE"), new StringValue(BorderLayout.BEFORE_FIRST_LINE));
        border.set(new StringValue("BEFORE_LINE_BEGINS"), new StringValue(BorderLayout.BEFORE_LINE_BEGINS));
        border.set(new StringValue("CENTER"), new StringValue(BorderLayout.CENTER));
        border.set(new StringValue("EAST"), new StringValue(BorderLayout.EAST));
        border.set(new StringValue("LINE_END"), new StringValue(BorderLayout.LINE_END));
        border.set(new StringValue("LINE_START"), new StringValue(BorderLayout.LINE_START));
        border.set(new StringValue("NORTH"), new StringValue(BorderLayout.NORTH));
        border.set(new StringValue("PAGE_END"), new StringValue(BorderLayout.PAGE_END));
        border.set(new StringValue("PAGE_START"), new StringValue(BorderLayout.PAGE_START));
        border.set(new StringValue("SOUTH"), new StringValue(BorderLayout.SOUTH));
        border.set(new StringValue("WEST"), new StringValue(BorderLayout.WEST));
        Variables.define("BorderLayout", border);

        final MapValue box = new MapValue(4);
        box.set(new StringValue("LINE_AXIS"), NumberValue.of(BoxLayout.LINE_AXIS));
        box.set(new StringValue("PAGE_AXIS"), NumberValue.of(BoxLayout.PAGE_AXIS));
        box.set(new StringValue("X_AXIS"), NumberValue.of(BoxLayout.X_AXIS));
        box.set(new StringValue("Y_AXIS"), NumberValue.of(BoxLayout.Y_AXIS));
        Variables.define("BoxLayout", box);
    }

    @Override
    public void init() {
        initConstants();
        // Components
        Functions.set("newButton", Components::newButton);
        Functions.set("newLabel", Components::newLabel);
        Functions.set("newPanel", Components::newPanel);
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
