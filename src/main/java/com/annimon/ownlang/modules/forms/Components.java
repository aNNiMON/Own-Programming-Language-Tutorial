package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Value;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Functions for working with components.
 */
public final class Components {

    private Components() { }

    static Value newWindow(Value... args) {
        Arguments.checkOrOr(0, 1, args.length);
        String title = (args.length == 1) ? args[0].asString() : "";
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return new JFrameValue(frame);
    }

    static Value newPanel(Value... args) {
        Arguments.checkOrOr(0, 1, args.length);
        final JPanel panel = new JPanel();
        if (args.length == 1) {
            panel.setLayout( ((LayoutManagerValue) args[0]).layout );
        }
        return new JPanelValue(panel);
    }

    static Value newButton(Value... args) {
        Arguments.checkOrOr(0, 1, args.length);
        String text = (args.length == 1) ? args[0].asString() : "";
        return new JButtonValue(new JButton(text));
    }

    static Value newLabel(Value... args) {
        Arguments.checkRange(0, 2, args.length);
        String text = (args.length >= 1) ? args[0].asString() : "";
        int align = (args.length == 2) ? args[1].asInt() : SwingConstants.LEADING;
        return new JLabelValue(new JLabel(text, align));
    }

    static Value newTextField(Value... args) {
        Arguments.checkOrOr(0, 1, args.length);
        String text = (args.length == 1) ? args[0].asString() : "";
        return new JTextFieldValue(new JTextField(text));
    }
}
