package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.awt.Dimension;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TextAlignment;
import javax.swing.JFrame;

/**
 *
 * @author aNNiMON
 */
public final class canvasfx implements Module {
    
    private static final int FX_EFFECT_TYPE = 5301;
    private static final int FX_COLOR_TYPE = 5302;
    
    private static JFrame frame;
    private static JFXPanel panel;
    private static GraphicsContext graphics;
    private static Canvas canvas;
    
    private static enum Events {
        DRAG_DETECTED(MouseEvent.DRAG_DETECTED),
        MOUSE_CLICKED(MouseEvent.MOUSE_CLICKED),
        MOUSE_DRAGGED(MouseEvent.MOUSE_DRAGGED),
        MOUSE_ENTERED(MouseEvent.MOUSE_ENTERED),
        MOUSE_ENTERED_TARGET(MouseEvent.MOUSE_ENTERED_TARGET),
        MOUSE_EXITED(MouseEvent.MOUSE_EXITED),
        MOUSE_EXITED_TARGET(MouseEvent.MOUSE_EXITED_TARGET),
        MOUSE_MOVED(MouseEvent.MOUSE_MOVED),
        MOUSE_PRESSED(MouseEvent.MOUSE_PRESSED),
        MOUSE_RELEASED(MouseEvent.MOUSE_RELEASED),
        
        KEY_PRESSED(KeyEvent.KEY_PRESSED),
        KEY_RELEASED(KeyEvent.KEY_RELEASED),
        KEY_TYPED(KeyEvent.KEY_TYPED),
        
        SWIPE_DOWN(SwipeEvent.SWIPE_DOWN),
        SWIPE_LEFT(SwipeEvent.SWIPE_LEFT),
        SWIPE_RIGHT(SwipeEvent.SWIPE_RIGHT),
        SWIPE_UP(SwipeEvent.SWIPE_UP);

        private final EventType<? extends Event> handler;

        private Events(EventType<? extends Event> handler) {
            this.handler = handler;
        }

        public EventType<? extends Event> getHandler() {
            return handler;
        }
    }

    @Override
    public void init() {
        Functions.set("window", new CreateWindow());
        Functions.set("repaint", new Repaint());
        
        // Color class
        final Map<Value, Value> colors = Arrays.stream(Color.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.getType().equals(Color.class))
                .collect(Collectors.toMap(
                        f -> new StringValue(f.getName()),
                        f -> {
                            try { return new ColorValue((Color) f.get(Color.class)); }
                            catch (IllegalAccessException ex) { return null; }
                        }));
        colors.put(new StringValue("new"), new FunctionValue(new newColor()));
        colors.put(new StringValue("rgb"), new FunctionValue(new rgbColor()));
        colors.put(new StringValue("hsb"), new FunctionValue(new hsbColor()));
        colors.put(new StringValue("web"), new FunctionValue(new webColor()));
        Variables.set("Color", new MapValue(colors));
        
        Functions.set("BlendEffect", new BlendEffect());
        Functions.set("BloomEffect", new BloomEffect());
        Functions.set("BoxBlurEffect", new BoxBlurEffect());
        Functions.set("ColorAdjustEffect", new ColorAdjustEffect());
        Functions.set("ColorInputEffect", new ColorInputEffect());
        Functions.set("DropShadowEffect", new DropShadowEffect());
        Functions.set("GaussianBlurEffect", new GaussianBlurEffect());
        Functions.set("GlowEffect", new GlowEffect());
        Functions.set("InnerShadowEffect", new InnerShadowEffect());
        Functions.set("LightingEffect", new LightingEffect());
        Functions.set("MotionBlurEffect", new MotionBlurEffect());
        Functions.set("PerspectiveTransformEffect", new PerspectiveTransformEffect());
        Functions.set("ReflectionEffect", new ReflectionEffect());
        Functions.set("SepiaToneEffect", new SepiaToneEffect());
        Functions.set("ShadowEffect", new ShadowEffect());
        
        Functions.set("addEventFilter", new addEventFilter());
        Functions.set("addEventHandler", new addEventHandler());
        Functions.set("applyEffect", new applyEffect());
        Functions.set("appendSVGPath", new appendSVGPath());
        Functions.set("arc", new arc());
        Functions.set("arcTo", new arcTo());
        Functions.set("beginPath", new beginPath());
        Functions.set("bezierCurveTo", new bezierCurveTo());
        Functions.set("clearRect", new clearRect());
        Functions.set("clip", new clip());
        Functions.set("closePath", new closePath());
        Functions.set("fill", new fill());
        Functions.set("fillArc", new fillArc());
        Functions.set("fillOval", new fillOval());
        Functions.set("fillPolygon", new fillPolygon());
        Functions.set("fillRect", new fillRect());
        Functions.set("fillRoundRect", new fillRoundRect());
        Functions.set("fillText", new fillText());
        Functions.set("getGlobalAlpha", new getGlobalAlpha());
        Functions.set("getLineWidth", new getLineWidth());
        Functions.set("getMiterLimit", new getMiterLimit());
        Functions.set("getFill", new getFill());
        Functions.set("getFillRule", new getFillRule());
        Functions.set("getGlobalAlpha", new getGlobalAlpha());
        Functions.set("getGlobalBlendMode", new getGlobalBlendMode());
        Functions.set("getLineCap", new getLineCap());
        Functions.set("getLineJoin", new getLineJoin());
        Functions.set("getLineWidth", new getLineWidth());
        Functions.set("getMiterLimit", new getMiterLimit());
        Functions.set("getStroke", new getStroke());
        Functions.set("getTextAlign", new getTextAlign());
        Functions.set("getTextBaseline", new getTextBaseline());
        Functions.set("isPointInPath", new isPointInPath());
        Functions.set("lineTo", new lineTo());
        Functions.set("moveTo", new moveTo());
        Functions.set("quadraticCurveTo", new quadraticCurveTo());
        Functions.set("rect", new rect());
        Functions.set("restore", new restore());
        Functions.set("rotate", new rotate());
        Functions.set("save", new save());
        Functions.set("scale", new scale());
        Functions.set("setEffect", new setEffect());
        Functions.set("setFill", new setFill());
        Functions.set("setFillRule", new setFillRule());
        Functions.set("setGlobalAlpha", new setGlobalAlpha());
        Functions.set("setGlobalBlendMode", new setGlobalBlendMode());
        Functions.set("setLineCap", new setLineCap());
        Functions.set("setLineJoin", new setLineJoin());
        Functions.set("setLineWidth", new setLineWidth());
        Functions.set("setMiterLimit", new setMiterLimit());
        Functions.set("setStroke", new setStroke());
        Functions.set("setTextAlign", new setTextAlign());
        Functions.set("setTextBaseline", new setTextBaseline());
        Functions.set("stroke", new stroke());
        Functions.set("strokeArc", new strokeArc());
        Functions.set("strokeLine", new strokeLine());
        Functions.set("strokeOval", new strokeOval());
        Functions.set("strokePolygon", new strokePolygon());
        Functions.set("strokePolyline", new strokePolyline());
        Functions.set("strokeRect", new strokeRect());
        Functions.set("strokeRoundRect", new strokeRoundRect());
        Functions.set("strokeText", new strokeText());
        Functions.set("transform", new transform());
        Functions.set("translate", new translate());

        final MapValue arcType = new MapValue(ArcType.values().length);
        for (ArcType value : ArcType.values()) {
            arcType.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("ArcType", arcType);
        
        final MapValue fillRule = new MapValue(FillRule.values().length);
        for (FillRule value : FillRule.values()) {
            fillRule.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("FillRule", fillRule);
        
        final MapValue blendMode = new MapValue(BlendMode.values().length);
        for (BlendMode value : BlendMode.values()) {
            blendMode.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("BlendMode", blendMode);
        
        final MapValue lineCap = new MapValue(StrokeLineCap.values().length);
        for (StrokeLineCap value : StrokeLineCap.values()) {
            lineCap.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("StrokeLineCap", lineCap);
        
        final MapValue lineJoin = new MapValue(StrokeLineJoin.values().length);
        for (StrokeLineJoin value : StrokeLineJoin.values()) {
            lineJoin.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("StrokeLineJoin", lineJoin);
        
        final MapValue textAlignment = new MapValue(TextAlignment.values().length);
        for (TextAlignment value : TextAlignment.values()) {
            textAlignment.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("TextAlignment", textAlignment);
        
        final MapValue vPos = new MapValue(VPos.values().length);
        for (VPos value : VPos.values()) {
            vPos.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("VPos", vPos);
        
        final MapValue events = new MapValue(Events.values().length);
        for (Events value : Events.values()) {
            events.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("Events", events);
        
        final MapValue mouseButton = new MapValue(MouseButton.values().length);
        for (MouseButton value : MouseButton.values()) {
            mouseButton.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("MouseButton", mouseButton);
        
        final MapValue keyCodes = new MapValue(KeyCode.values().length);
        for (KeyCode value : KeyCode.values()) {
            keyCodes.set(new StringValue(value.name()), NumberValue.of(value.ordinal()));
        }
        Variables.set("KeyCode", keyCodes);
    }
    
    private static class ColorValue implements Value {
        
        private final Color color;
        
        public ColorValue(Color effect) {
            this.color = effect;
        }
        
        @Override
        public Object raw() {
            return color;
        }
        
        @Override
        public int asInt() {
            final int a = (int) (color.getOpacity() * 255) & 0xFF;
            final int r = (int) (color.getRed()* 255) & 0xFF;
            final int g = (int) (color.getGreen()* 255) & 0xFF;
            final int b = (int) (color.getBlue()* 255) & 0xFF;
            return ((a << 24) | (r << 16) | (g << 8) | b);
        }
        
        @Override
        public double asNumber() {
            return asInt();
        }
        
        @Override
        public String asString() {
            return color.toString();
        }
        
        @Override
        public int type() {
            return FX_COLOR_TYPE;
        }
        
        @Override
        public int compareTo(Value o) {
            // we don't need this
            return 0;
        }
        
        @Override
        public String toString() {
            return "JavaFX Color " + color;
        }
    }
    
    private static class newColor implements Function {

        @Override
        public Value execute(Value... args) {
            double r, g, b, opacity;
            if (args.length == 1) {
                final int color = args[0].asInt();
                r = ((color >> 16) & 0xFF) / 255.0;
                g = ((color >> 8) & 0xFF) / 255.0;
                b = (color & 0xFF) / 255.0;
                opacity = ((color >> 24) & 0xFF) / 255.0;
            } else {
                r = args[0].asNumber();
                g = args[1].asNumber();
                b = args[2].asNumber();
                opacity = (args.length >= 4) ? args[3].asNumber() : 1d;
            }
            return new ColorValue(new Color(r, g, b, opacity));
        }
    }
    
    private static class rgbColor implements Function {

        @Override
        public Value execute(Value... args) {
            int r = args[0].asInt();
            int g = args[1].asInt();
            int b = args[2].asInt();
            double opacity = (args.length >= 4) ? args[3].asNumber() : 1d;
            return new ColorValue(Color.rgb(r, g, b, opacity));
        }
    }
    
    private static class hsbColor implements Function {

        @Override
        public Value execute(Value... args) {
            double h = args[0].asNumber();
            double s = args[1].asNumber();
            double b = args[2].asNumber();
            double opacity = (args.length >= 4) ? args[3].asNumber() : 1d;
            return new ColorValue(Color.hsb(h, s, b, opacity));
        }
    }
    
    private static class webColor implements Function {

        @Override
        public Value execute(Value... args) {
            return new ColorValue(Color.web(args[0].asString(),
                    (args.length >= 2) ? args[1].asNumber() : 1d ));
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Effects">
    private static class EffectValue implements Value {
        
        private final Effect effect;
        
        public EffectValue(Effect effect) {
            this.effect = effect;
        }
        
        @Override
        public Object raw() {
            return effect;
        }
        
        @Override
        public int asInt() {
            throw new TypeException("Cannot cast JavaFX Effect to integer");
        }
        
        @Override
        public double asNumber() {
            throw new TypeException("Cannot cast JavaFX Effect to number");
        }
        
        @Override
        public String asString() {
            return effect.toString();
        }
        
        @Override
        public int type() {
            return FX_EFFECT_TYPE;
        }
        
        @Override
        public int compareTo(Value o) {
            // we don't need this
            return 0;
        }
        
        @Override
        public String toString() {
            return "JavaFX Effect " + effect;
        }
    }
    
    private static class BlendEffect implements Function {
        @Override
        public Value execute(Value... args) {
            Blend effect = new Blend();
            if (args.length >= 1) {
                effect.setMode(BlendMode.values()[args[0].asInt()]);
            }
            if (args.length >= 3) {
                effect.setBottomInput((Effect)args[1].raw());
                effect.setTopInput((Effect)args[2].raw());
            }
            if (args.length >= 4) {
                effect.setOpacity(args[3].asNumber());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class BloomEffect implements Function {
        @Override
        public Value execute(Value... args) {
            Bloom effect = new Bloom();
            if (args.length >= 1) {
                effect.setThreshold(args[0].asNumber());
            }
            if (args.length >= 2) {
                effect.setInput((Effect)args[1].raw());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class BoxBlurEffect implements Function {
        @Override
        public Value execute(Value... args) {
            BoxBlur effect = new BoxBlur();
            if (args.length >= 3) {
                effect.setWidth(args[0].asNumber());
                effect.setHeight(args[1].asNumber());
                effect.setIterations(args[2].asInt());
            }
            if (args.length >= 4) {
                effect.setInput((Effect)args[3].raw());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class ColorAdjustEffect implements Function {
        @Override
        public Value execute(Value... args) {
            return new EffectValue(new ColorAdjust(
                    args[0].asNumber(), args[1].asNumber(), args[2].asNumber(), args[3].asNumber()));
        }
    }
    
    private static class ColorInputEffect implements Function {
        @Override
        public Value execute(Value... args) {
            return new EffectValue(new ColorInput(
                    args[0].asNumber(), args[1].asNumber(), args[2].asNumber(), args[3].asNumber(), (Color) args[4].raw()));
        }
    }
    
    private static class DropShadowEffect implements Function {
        @Override
        public Value execute(Value... args) {
            DropShadow effect;
            switch (args.length) {
                case 2:
                    effect = new DropShadow(args[0].asNumber(), (Color) args[1].raw());
                    break;
                case 4:
                    effect = new DropShadow(args[0].asNumber(),
                            args[1].asInt(), args[2].asInt(),
                            (Color) args[3].raw());
                    break;
                case 6:
                    effect = new DropShadow(BlurType.values()[args[0].asInt()], (Color) args[1].raw(),
                            args[2].asNumber(), args[3].asNumber(), args[4].asNumber(), args[5].asNumber());
                    break;
                default:
                    effect = new DropShadow();
            }
            return new EffectValue(effect);
        }
    }
    
    private static class GaussianBlurEffect implements Function {
        @Override
        public Value execute(Value... args) {
            GaussianBlur effect = new GaussianBlur();
            if (args.length >= 1) {
                effect.setRadius(args[0].asNumber());
            }
            if (args.length >= 2) {
                effect.setInput((Effect)args[1].raw());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class GlowEffect implements Function {
        @Override
        public Value execute(Value... args) {
            Glow effect = new Glow();
            if (args.length >= 1) {
                effect.setLevel(args[0].asNumber());
            }
            if (args.length >= 2) {
                effect.setInput((Effect)args[1].raw());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class InnerShadowEffect implements Function {
        @Override
        public Value execute(Value... args) {
            InnerShadow effect;
            switch (args.length) {
                case 2:
                    effect = new InnerShadow(args[0].asNumber(), (Color) args[1].raw());
                    break;
                case 4:
                    effect = new InnerShadow(args[0].asNumber(),
                            args[1].asInt(), args[2].asInt(),
                            (Color) args[3].raw());
                    break;
                case 6:
                    effect = new InnerShadow(BlurType.values()[args[0].asInt()], (Color) args[1].raw(),
                            args[2].asNumber(), args[3].asNumber(), args[4].asNumber(), args[5].asNumber());
                    break;
                default:
                    effect = new InnerShadow();
            }
            return new EffectValue(effect);
        }
    }
    
    private static class LightingEffect implements Function {
        @Override
        public Value execute(Value... args) {
            Light light;
            final ArrayValue l = (ArrayValue) args[0];
            switch (l.size()) {
                case 3:
                    light = new Light.Distant(l.get(0).asNumber(), l.get(1).asNumber(), (Color) l.get(2).raw());
                    break;
                case 4:
                    light = new Light.Point(l.get(0).asNumber(), l.get(1).asNumber(), l.get(2).asNumber(), (Color) l.get(3).raw());
                    break;
                case 5:
                    light = new Light.Spot(l.get(0).asNumber(), l.get(1).asNumber(), l.get(2).asNumber(), l.get(3).asNumber(), (Color) l.get(4).raw());
                    break;
                default:
                    light = null;
            }
            Lighting effect = new Lighting(light);
            if (args.length >= 2) {
                effect.setSurfaceScale(args[1].asNumber());
            }
            if (args.length >= 3) {
                effect.setDiffuseConstant(args[2].asNumber());
            }
            if (args.length >= 5) {
                effect.setSpecularConstant(args[3].asNumber());
                effect.setSpecularExponent(args[4].asNumber());
            }
            if (args.length >= 7) {
                effect.setBumpInput((Effect) args[5].raw());
                effect.setContentInput((Effect) args[6].raw());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class MotionBlurEffect implements Function {
        @Override
        public Value execute(Value... args) {
            MotionBlur effect = new MotionBlur();
            if (args.length >= 2) {
                effect.setAngle(args[0].asNumber());
                effect.setRadius(args[1].asNumber());
            }
            if (args.length >= 3) {
                effect.setInput((Effect)args[2].raw());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class PerspectiveTransformEffect implements Function {
        @Override
        public Value execute(Value... args) {
            return new EffectValue(new PerspectiveTransform(
                    args[0].asNumber(), args[1].asNumber(), args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber(), args[6].asNumber(), args[7].asNumber() ));
        }
    }
    
    private static class ReflectionEffect implements Function {
        @Override
        public Value execute(Value... args) {
            return new EffectValue(new Reflection(
                    args[0].asNumber(), args[1].asNumber(), args[2].asNumber(), args[3].asNumber()));
        }
    }
    
    private static class SepiaToneEffect implements Function {
        @Override
        public Value execute(Value... args) {
            SepiaTone effect = new SepiaTone();
            if (args.length >= 1) {
                effect.setLevel(args[0].asNumber());
            }
            if (args.length >= 2) {
                effect.setInput((Effect)args[1].raw());
            }
            return new EffectValue(effect);
        }
    }
    
    private static class ShadowEffect implements Function {
        @Override
        public Value execute(Value... args) {
            Shadow effect;
            switch (args.length) {
                case 2:
                    effect = new Shadow(args[0].asNumber(), (Color) args[1].raw());
                    break;
                case 3:
                    effect = new Shadow(BlurType.values()[args[0].asInt()], (Color) args[1].raw(),
                            args[2].asNumber());
                    break;
                default:
                    effect = new Shadow();
            }
            return new EffectValue(effect);
        }
    }
//</editor-fold>
    
    private static class applyEffect implements Function {
        @Override
        public Value execute(Value... args) {
            if (args[0].type() != FX_EFFECT_TYPE) {
                throw new TypeException("Effect expected, found " + Types.typeToString(args[0].type()));
            }
            graphics.applyEffect((Effect) args[0].raw());
            return NumberValue.ZERO;
        }
    }
    
    private static class appendSVGPath implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.appendSVGPath(args[0].asString());
            return NumberValue.ZERO;
        }
    }
    
    private static class arc implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.arc(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class arcTo implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.arcTo(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class beginPath implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.beginPath();
            return NumberValue.ZERO;
        }
    }
    
    private static class bezierCurveTo implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.bezierCurveTo(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class clearRect implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.clearRect(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class clip implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.clip();
            return NumberValue.ZERO;
        }
    }
    
    private static class closePath implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.closePath();
            return NumberValue.ZERO;
        }
    }
    
    private static class fill implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.fill();
            return NumberValue.ZERO;
        }
    }
    
    private static class fillArc implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.fillArc(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber(),
                    ArcType.values()[args[6].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class fillOval implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.fillOval(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class fillPolygon implements Function {
        @Override
        public Value execute(Value... args) {
            final ArrayValue xarr = (ArrayValue) args[0];
            final ArrayValue yarr = (ArrayValue) args[1];

            final int size = xarr.size();
            final double[] xPoints = new double[size];
            final double[] yPoints = new double[size];
            for (int i = 0; i < size; i++) {
                xPoints[i] = xarr.get(i).asNumber();
                yPoints[i] = yarr.get(i).asNumber();
            }
            
            graphics.fillPolygon(xPoints, yPoints, args[2].asInt());
            return NumberValue.ZERO;
        }
    }
    
    private static class fillRect implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.fillRect(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class fillRoundRect implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.fillRoundRect(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber() );
            return NumberValue.ZERO;
        }
    }
    
    private static class fillText implements Function {
        @Override
        public Value execute(Value... args) {
            if (args.length < 4) {
                // str x y
                graphics.fillText(args[0].asString(), args[1].asNumber(),
                        args[2].asNumber());
            } else {
                graphics.fillText(args[0].asString(), args[1].asNumber(),
                        args[2].asNumber(), args[3].asNumber());
            }
            return NumberValue.ZERO;
        }
    }
    
    private static class getFill implements Function {
        @Override
        public Value execute(Value... args) {
            return new ColorValue((Color)graphics.getFill());
        }
    }
    
    private static class getFillRule implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getFillRule().ordinal());
        }
    }
    
    private static class getGlobalAlpha implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getGlobalAlpha());
        }
    }
    
    private static class getGlobalBlendMode implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getGlobalBlendMode().ordinal());
        }
    }
    
    private static class getLineCap implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getLineCap().ordinal());
        }
    }
    
    private static class getLineJoin implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getLineJoin().ordinal());
        }
    }
    
    private static class getLineWidth implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getLineWidth());
        }
    }
    
    private static class getMiterLimit implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getMiterLimit());
        }
    }
    
    private static class getStroke implements Function {
        @Override
        public Value execute(Value... args) {
            return new ColorValue((Color)graphics.getStroke());
        }
    }
    
    private static class getTextAlign implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getTextAlign().ordinal());
        }
    }
    
    private static class getTextBaseline implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.of(graphics.getTextBaseline().ordinal());
        }
    }
    
    private static class isPointInPath implements Function {
        @Override
        public Value execute(Value... args) {
            return NumberValue.fromBoolean(graphics.isPointInPath(args[0].asNumber(), args[1].asNumber()));
        }
    }
    
    private static class lineTo implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.lineTo(args[0].asNumber(), args[1].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class moveTo implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.moveTo(args[0].asNumber(), args[1].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class quadraticCurveTo implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.quadraticCurveTo(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class rect implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.rect(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class restore implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.restore();
            return NumberValue.ZERO;
        }
    }
    
    private static class rotate implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.rotate(args[0].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class save implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.save();
            return NumberValue.ZERO;
        }
    }
    
    private static class scale implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.scale(args[0].asNumber(), args[1].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class setEffect implements Function {
        @Override
        public Value execute(Value... args) {
            if (args[0].type() != FX_EFFECT_TYPE) {
                throw new TypeException("Effect expected, found " + Types.typeToString(args[0].type()));
            }
            graphics.setEffect((Effect) args[0].raw());
            return NumberValue.ZERO;
        }
    }
    
    private static class setFill implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setFill((Color) args[0].raw());
            return NumberValue.ZERO;
        }
    }
    
    private static class setFillRule implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setFillRule(FillRule.values()[args[0].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class setGlobalAlpha implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setGlobalAlpha(args[0].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class setGlobalBlendMode implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setGlobalBlendMode(BlendMode.values()[args[0].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class setLineCap implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setLineCap(StrokeLineCap.values()[args[0].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class setLineJoin implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setLineJoin(StrokeLineJoin.values()[args[0].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class setLineWidth implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setLineWidth(args[0].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class setMiterLimit implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setMiterLimit(args[0].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class setStroke implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setStroke((Color) args[0].raw());
            return NumberValue.ZERO;
        }
    }
    
    private static class setTextAlign implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setTextAlign(TextAlignment.values()[args[0].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class setTextBaseline implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.setTextBaseline(VPos.values()[args[0].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class stroke implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.stroke();
            return NumberValue.ZERO;
        }
    }
    
    private static class strokeArc implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.strokeArc(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber(),
                    ArcType.values()[args[6].asInt()]);
            return NumberValue.ZERO;
        }
    }
    
    private static class strokeLine implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.strokeLine(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class strokeOval implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.strokeOval(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class strokePolygon implements Function {
        @Override
        public Value execute(Value... args) {
            final ArrayValue xarr = (ArrayValue) args[0];
            final ArrayValue yarr = (ArrayValue) args[1];

            final int size = xarr.size();
            final double[] xPoints = new double[size];
            final double[] yPoints = new double[size];
            for (int i = 0; i < size; i++) {
                xPoints[i] = xarr.get(i).asNumber();
                yPoints[i] = yarr.get(i).asNumber();
            }
            
            graphics.strokePolygon(xPoints, yPoints, args[2].asInt());
            return NumberValue.ZERO;
        }
    }
    
    private static class strokePolyline implements Function {
        @Override
        public Value execute(Value... args) {
            final ArrayValue xarr = (ArrayValue) args[0];
            final ArrayValue yarr = (ArrayValue) args[1];

            final int size = xarr.size();
            final double[] xPoints = new double[size];
            final double[] yPoints = new double[size];
            for (int i = 0; i < size; i++) {
                xPoints[i] = xarr.get(i).asNumber();
                yPoints[i] = yarr.get(i).asNumber();
            }
            
            graphics.strokePolyline(xPoints, yPoints, args[2].asInt());
            return NumberValue.ZERO;
        }
    }
    
    private static class strokeRect implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.strokeRect(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class strokeRoundRect implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.strokeRoundRect(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber() );
            return NumberValue.ZERO;
        }
    }
    
    private static class strokeText implements Function {
        @Override
        public Value execute(Value... args) {
            if (args.length < 4) {
                // str x y
                graphics.strokeText(args[0].asString(), args[1].asNumber(),
                        args[2].asNumber());
            } else {
                graphics.strokeText(args[0].asString(), args[1].asNumber(),
                        args[2].asNumber(), args[3].asNumber());
            }
            return NumberValue.ZERO;
        }
    }
    
    private static class transform implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.transform(args[0].asNumber(), args[1].asNumber(),
                    args[2].asNumber(), args[3].asNumber(),
                    args[4].asNumber(), args[5].asNumber());
            return NumberValue.ZERO;
        }
    }
    
    private static class translate implements Function {
        @Override
        public Value execute(Value... args) {
            graphics.translate(args[0].asNumber(), args[1].asNumber());
            return NumberValue.ZERO;
        }
    }

    private static class CreateWindow implements Function {

        @Override
        public Value execute(Value... args) {
            String title = "";
            int width = 640;
            int height = 480;
            switch (args.length) {
                case 1:
                    title = args[0].asString();
                    break;
                case 2:
                    width = args[0].asInt();
                    height = args[1].asInt();
                    break;
                case 3:
                    title = args[0].asString();
                    width = args[1].asInt();
                    height = args[2].asInt();
                    break;
            }
            panel = new JFXPanel();
            panel.setPreferredSize(new Dimension(width, height));
            panel.setFocusable(true);
            canvas = new Canvas(width, height);
            canvas.setFocusTraversable(true);
            canvas.requestFocus();
            graphics = canvas.getGraphicsContext2D();
            
            frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
            
            Platform.runLater(() -> {
                Group root = new Group();
                Scene scene = new Scene(root, Color.WHITE);
                root.getChildren().add(canvas);
                panel.setScene(scene);
            });
            return NumberValue.ZERO;
        }
    }
    
    private static class Repaint implements Function {

        @Override
        public Value execute(Value... args) {
            panel.invalidate();
            panel.repaint();
            return NumberValue.ZERO;
        }
    }
    
    private static class addEventFilter implements Function {
        @Override
        public Value execute(Value... args) {
            final Function handler = ((FunctionValue) args[1]).getValue();
            final Events event = Events.values()[args[0].asInt()];
            canvas.addEventFilter(event.getHandler(), e -> handleEvent(e, handler));
            return NumberValue.ZERO;
        }
    }
    
    private static class addEventHandler implements Function {
        @Override
        public Value execute(Value... args) {
            final Function handler = ((FunctionValue) args[1]).getValue();
            final Events event = Events.values()[args[0].asInt()];
            canvas.addEventHandler(event.getHandler(), e -> handleEvent(e, handler));
            return NumberValue.ZERO;
        }
    }
    
    private static void handleEvent(Event event, final Function handler) {
        if (event instanceof MouseEvent) {
            handleMouseEvent((MouseEvent) event, handler);
        } else if (event instanceof KeyEvent) {
            handleKeyEvent((KeyEvent) event, handler);
        } else if (event instanceof DragEvent) {
            handleDragEvent((DragEvent) event, handler);
        }
    }
    
    private static void handleMouseEvent(MouseEvent e, final Function handler) {
        final MapValue map = new MapValue(25);
        map.set(new StringValue("button"), NumberValue.of(e.getButton().ordinal()));
        map.set(new StringValue("clickCount"), NumberValue.of(e.getClickCount()));
        map.set(new StringValue("sceneX"), NumberValue.of(e.getSceneX()));
        map.set(new StringValue("sceneY"), NumberValue.of(e.getSceneY()));
        map.set(new StringValue("screenX"), NumberValue.of(e.getScreenX()));
        map.set(new StringValue("screenY"), NumberValue.of(e.getScreenY()));
        map.set(new StringValue("x"), NumberValue.of(e.getX()));
        map.set(new StringValue("y"), NumberValue.of(e.getY()));
        map.set(new StringValue("z"), NumberValue.of(e.getZ()));
        map.set(new StringValue("isAltDown"), NumberValue.fromBoolean(e.isAltDown()));
        map.set(new StringValue("isConsumed"), NumberValue.fromBoolean(e.isConsumed()));
        map.set(new StringValue("isControlDown"), NumberValue.fromBoolean(e.isControlDown()));
        map.set(new StringValue("isDragDetect"), NumberValue.fromBoolean(e.isDragDetect()));
        map.set(new StringValue("isMetaDown"), NumberValue.fromBoolean(e.isMetaDown()));
        map.set(new StringValue("isMiddleButtonDown"), NumberValue.fromBoolean(e.isMiddleButtonDown()));
        map.set(new StringValue("isPopupTrigger"), NumberValue.fromBoolean(e.isPopupTrigger()));
        map.set(new StringValue("isPrimaryButtonDown"), NumberValue.fromBoolean(e.isPrimaryButtonDown()));
        map.set(new StringValue("isSecondaryButtonDown"), NumberValue.fromBoolean(e.isSecondaryButtonDown()));
        map.set(new StringValue("isShiftDown"), NumberValue.fromBoolean(e.isShiftDown()));
        map.set(new StringValue("isShortcutDown"), NumberValue.fromBoolean(e.isShortcutDown()));
        map.set(new StringValue("isStillSincePress"), NumberValue.fromBoolean(e.isStillSincePress()));
        map.set(new StringValue("isSynthesized"), NumberValue.fromBoolean(e.isSynthesized()));
        handler.execute(map);
    }
    
    private static void handleKeyEvent(final KeyEvent e, final Function handler) {
        final MapValue map = new MapValue(10);
        map.set(new StringValue("code"), NumberValue.of(e.getCode().ordinal()));
        map.set(new StringValue("character"), new StringValue(e.getCharacter()));
        map.set(new StringValue("text"), new StringValue(e.getText()));
        map.set(new StringValue("isAltDown"), NumberValue.fromBoolean(e.isAltDown()));
        map.set(new StringValue("isConsumed"), NumberValue.fromBoolean(e.isConsumed()));
        map.set(new StringValue("isControlDown"), NumberValue.fromBoolean(e.isControlDown()));
        map.set(new StringValue("isMetaDown"), NumberValue.fromBoolean(e.isMetaDown()));
        map.set(new StringValue("isShiftDown"), NumberValue.fromBoolean(e.isShiftDown()));
        map.set(new StringValue("isShortcutDown"), NumberValue.fromBoolean(e.isShortcutDown()));
        handler.execute(map);
    }
    
    private static void handleDragEvent(final DragEvent e, final Function handler) {
        final MapValue map = new MapValue(10);
        map.set(new StringValue("sceneX"), NumberValue.of(e.getSceneX()));
        map.set(new StringValue("sceneY"), NumberValue.of(e.getSceneY()));
        map.set(new StringValue("screenX"), NumberValue.of(e.getScreenX()));
        map.set(new StringValue("screenY"), NumberValue.of(e.getScreenY()));
        map.set(new StringValue("x"), NumberValue.of(e.getX()));
        map.set(new StringValue("y"), NumberValue.of(e.getY()));
        map.set(new StringValue("z"), NumberValue.of(e.getZ()));
        map.set(new StringValue("isAccepted"), NumberValue.fromBoolean(e.isAccepted()));
        map.set(new StringValue("isConsumed"), NumberValue.fromBoolean(e.isConsumed()));
        map.set(new StringValue("isDropCompleted"), NumberValue.fromBoolean(e.isDropCompleted()));
        handler.execute(map);
    }
    
}
