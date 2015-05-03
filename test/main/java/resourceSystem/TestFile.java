package resourceSystem;

import base.Resource;

public class TestFile implements Resource {
    int intField;
    String strField;

    TestFile() {
        intField = 0;
        strField = "";
    }

    public int getIntField() {
        return intField;
    }

    public String getStrField() {
        return strField;
    }
}
