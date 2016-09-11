package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author aNNiMON
 */
public final class date implements Module {

    private static final int DATE_DATE_FORMAT = 9829;

    private static final StringValue
            YEAR = new StringValue("year"),
            MONTH = new StringValue("month"),
            DAY = new StringValue("day"),
            HOUR = new StringValue("hour"),
            MINUTE = new StringValue("minute"),
            SECOND = new StringValue("second"),
            MILLISECOND = new StringValue("millisecond");

    public static void initConstants() {
        Variables.define("STYLE_FULL", NumberValue.of(DateFormat.FULL));
        Variables.define("STYLE_LONG", NumberValue.of(DateFormat.LONG));
        Variables.define("STYLE_MEDIUM", NumberValue.of(DateFormat.MEDIUM));
        Variables.define("STYLE_SHORT", NumberValue.of(DateFormat.SHORT));
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("newDate", new date_newDate());
        Functions.set("newFormat", new date_newFormat());
        Functions.set("formatDate", new date_format());
        Functions.set("parseDate", new date_parse());
        Functions.set("toTimestamp", new date_toTimestamp());
    }

    //<editor-fold defaultstate="collapsed" desc="Values">
    private static class DateValue extends MapValue {

        private static DateValue from(Date date) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return from(calendar);
        }

        private static DateValue from(Calendar calendar) {
            final DateValue value = new DateValue(calendar);
            value.set(YEAR, NumberValue.of(calendar.get(Calendar.YEAR)));
            value.set(MONTH, NumberValue.of(calendar.get(Calendar.MONTH)));
            value.set(DAY, NumberValue.of(calendar.get(Calendar.DAY_OF_MONTH)));
            value.set(HOUR, NumberValue.of(calendar.get(Calendar.HOUR)));
            value.set(MINUTE, NumberValue.of(calendar.get(Calendar.MINUTE)));
            value.set(SECOND, NumberValue.of(calendar.get(Calendar.SECOND)));
            value.set(MILLISECOND, NumberValue.of(calendar.get(Calendar.MILLISECOND)));
            return value;
        }

        private final Calendar value;

        private DateValue(Calendar value) {
            super(8);
            this.value = value;
        }

        @Override
        public Object raw() {
            return value;
        }

        @Override
        public int asInt() {
            throw new TypeException("Cannot cast Date to integer");
        }

        @Override
        public double asNumber() {
            throw new TypeException("Cannot cast Date to number");
        }

        @Override
        public String asString() {
            return String.format("%04d-%02d-%02d %02d:%02d:%02d.%03d",
                    get(YEAR).asInt(), get(MONTH).asInt(), get(DAY).asInt(),
                    get(HOUR).asInt(), get(MINUTE).asInt(), get(SECOND).asInt(),
                    get(MILLISECOND).asInt());
        }

        @Override
        public int compareTo(Value o) {
            if (o.type() == Types.MAP && (o.raw() instanceof Calendar)) {
                return value.compareTo((Calendar) o.raw());
            }
            return asString().compareTo(o.asString());
        }
    }

    private static class DateFormatValue implements Value {

        private final DateFormat value;

        public DateFormatValue(DateFormat value) {
            this.value = value;
        }

        @Override
        public Object raw() {
            return value;
        }

        @Override
        public int asInt() {
            throw new TypeException("Cannot cast DateFormat to integer");
        }

        @Override
        public double asNumber() {
            throw new TypeException("Cannot cast DateFormat to number");
        }

        @Override
        public String asString() {
            return value.toString();
        }

        @Override
        public int type() {
            return DATE_DATE_FORMAT;
        }

        @Override
        public int compareTo(Value o) {
            return 0;
        }

        @Override
        public String toString() {
            return "DateFormat " + value.toString();
        }
    }
//</editor-fold>

    private static class date_newDate implements Function {

        @Override
        public Value execute(Value... args) {
            final Calendar calendar = Calendar.getInstance();
            calendar.clear();
            switch (args.length) {
                case 0:
                    // date()
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    break;
                case 1:
                    // date(timestamp)
                    // date("date")
                    date(calendar, args[0]);
                    break;
                case 2:
                    // date("pattern", "date")
                    date(calendar, args[0], args[1]);
                    break;
                case 3:
                case 4:
                    // date(year, month, day)
                    calendar.set(args[0].asInt(), args[1].asInt(), args[2].asInt());
                    break;
                case 5:
                    // date(year, month, day, hour, minute)
                    calendar.set(args[0].asInt(), args[1].asInt(), args[2].asInt(),
                            args[3].asInt(), args[4].asInt());
                    break;
                case 6:
                default:
                    // date(year, month, day, hour, minute, second)
                    calendar.set(args[0].asInt(), args[1].asInt(), args[2].asInt(),
                            args[3].asInt(), args[4].asInt(), args[5].asInt());
                    break;
            }
            return DateValue.from(calendar);
        }
        
        private static void date(Calendar calendar, Value arg1) {
            if (arg1.type() == Types.NUMBER) {
                calendar.setTimeInMillis(((NumberValue) arg1).asLong());
                return;
            }
            try {
                calendar.setTime(DateFormat.getDateTimeInstance().parse(arg1.asString()));
            } catch (ParseException ignore) { }
        }

        private static void date(Calendar calendar, Value arg1, Value arg2) {
            if (arg1.type() == Types.NUMBER) {
                date(calendar, arg1);
                return;
            }
            try {
                calendar.setTime(new SimpleDateFormat(arg1.asString()).parse(arg2.asString()));
            } catch (ParseException ignore) { }
        }
    }

    private static class date_newFormat implements Function {

        @Override
        public Value execute(Value... args) {
            if (args.length == 0) {
                return new DateFormatValue(new SimpleDateFormat());
            }
            if (args.length == 1) {
                if (args[0].type() == Types.STRING) {
                    return new DateFormatValue(new SimpleDateFormat(args[0].asString()));
                }
                switch (args[0].asInt()) {
                    case 0:
                        return new DateFormatValue(DateFormat.getInstance());
                    case 1:
                        return new DateFormatValue(DateFormat.getDateInstance());
                    case 2:
                        return new DateFormatValue(DateFormat.getTimeInstance());
                    case 3:
                    default:
                        return new DateFormatValue(DateFormat.getDateTimeInstance());
                }
            }

            if (args[0].type() == Types.STRING) {
                return new DateFormatValue(new SimpleDateFormat(args[0].asString(), new Locale(args[1].asString())));
            }
            switch (args[0].asInt()) {
                case 0:
                    return new DateFormatValue(DateFormat.getInstance());
                case 1:
                    return new DateFormatValue(DateFormat.getDateInstance(args[1].asInt()));
                case 2:
                    return new DateFormatValue(DateFormat.getTimeInstance(args[1].asInt()));
                case 3:
                default:
                    int dateStyle = args[1].asInt();
                    int timeStyle = (args.length > 2) ? args[2].asInt() : dateStyle;
                    return new DateFormatValue(DateFormat.getDateTimeInstance(dateStyle, timeStyle));
            }
        }
    }

    private static class date_parse implements Function {

        @Override
        public Value execute(Value... args) {
            Arguments.checkOrOr(1, 2, args.length);

            final DateFormat format;
            if (args.length == 1) {
                format = new SimpleDateFormat();
            } else {
                if (args[1].type() != DATE_DATE_FORMAT) {
                    throw new TypeException("DateFormat expected, found " + Types.typeToString(args[1].type()));
                }
                format = (DateFormat) args[1].raw();
            }

            try {
                return DateValue.from(format.parse(args[0].asString()));
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class date_format implements Function {

        @Override
        public Value execute(Value... args) {
            Arguments.checkOrOr(1, 2, args.length);

            final DateFormat format;
            if (args.length == 1) {
                format = new SimpleDateFormat();
            } else {
                if (args[1].type() != DATE_DATE_FORMAT) {
                    throw new TypeException("DateFormat expected, found " + Types.typeToString(args[1].type()));
                }
                format = (DateFormat) args[1].raw();
            }

            return new StringValue(format.format( ((Calendar) args[0].raw()).getTime() ));
        }
    }

    private static class date_toTimestamp implements Function {

        @Override
        public Value execute(Value... args) {
            Arguments.check(1, args.length);
            return NumberValue.of(((Calendar) args[0].raw()).getTimeInMillis() );
        }
    }
}
