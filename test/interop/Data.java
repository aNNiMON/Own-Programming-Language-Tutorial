package interop;

public final class Data {
    public final int intValue = 3228;
    public final int[] intArrayValue = {1, 2, 3};
    public final Object nullObject = null;
    public final String stringValue = "str";
    public final String[] stringArrayValue = {"abc", "test"};
    public final Object[] objectArray = new Object[] {intValue, intArrayValue, nullObject, stringValue, stringArrayValue};
    public final Object compoundObject = objectArray;

    public void method() {
        System.out.println("method");
    }

    public String methodWithResult() {
        return "result";
    }


    private int value;
    private String text;

    public void set(int value) {
        this.value = value;
    }

    public void set(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
