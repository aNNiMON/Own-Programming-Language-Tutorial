package com.annimon.ownlang.modules.forms;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Value;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Functions for working with components.
 */
public final class Components {

    private Components() { }

    static Value newWindow(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        String title = (args.length == 1) ? args[0].asString() : "";
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return new JFrameValue(frame);
    }

    static Value newPanel(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        final JPanel panel = new JPanel();
        if (args.length == 1) {
            panel.setLayout( ((LayoutManagerValue) args[0]).layout );
        }
        return new JPanelValue(panel);
    }

    static Value newButton(Value[] args) {
        Arguments.checkOrOr(0, 1, args.length);
        String text = (args.length == 1) ? args[0].asString() : "";
        return new JButtonValue(new JButton(text));
    }

    static Value newLabel(Value[] args) {
        Arguments.checkRange(0, 2, args.length);
        String text = (args.length >= 1) ? args[0].asString() : "";
        int align = (args.length == 2) ? args[1].asInt() : SwingConstants.LEADING;
        return new JLabelValue(new JLabel(text, align));
    }

    static Value newTextField(Value[] args) {
        Arguments.checkRange(0, 2, args.length);
        String text = "";
        int cols = 0;
        switch (args.length) {
            case 1: {
                text = args[0].asString();
            } break;
            case 2: {
                text = args[0].asString();
                cols = args[1].asInt();
            } break;
        }
        return new JTextFieldValue(new JTextField(text, cols));
    }
    
    static Value newProgressBar(Value[] args) {
        Arguments.checkRange(0, 3, args.length);
        boolean isVertical = false;
        int min = 0;
        int max = 100;
        switch (args.length) {
            case 1: {
                isVertical = args[0].asInt() != 0;
            } break;
            case 2: {
                min = args[0].asInt();
                max = args[1].asInt();
            } break;
            case 3: {
                isVertical = args[0].asInt() != 0;
                min = args[1].asInt();
                max = args[2].asInt();
            } break;
        }
        return new JProgressBarValue(new JProgressBar(
                isVertical ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL,
                min, max
        ));
    }
}
