package net.morimekta.providence.util.json;

/**
 * @author Stein Eldar Johnsen
 * @since 16.01.16.
 */
class JsonContext {
    protected enum Mode {
        VALUE,
        LIST,
        MAP,
    }

    protected enum Expect {
        KEY,
        VALUE,
    }

    public final Mode   mode;
    public Expect expect;
    public int    num;

    public JsonContext(Mode mode) {
        this.mode = mode;
        if (mode == Mode.MAP) {
            expect = Expect.KEY;
        } else {
            expect = Expect.VALUE;
        }
        num = 0;
    }

    public boolean key() {
        return mode == Mode.MAP && expect == Expect.KEY;
    }

    public boolean value() {
        return mode == Mode.VALUE ? num == 0 : !key();
    }

    public boolean map() {
        return mode == Mode.MAP;
    }

    public boolean list() {
        return mode == Mode.LIST;
    }
}
