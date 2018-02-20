/*
 * @test /nodynamiccopyright/
 * @summary Check that null can be casted to a value type; but cannot be compared with value types.
 *
 * @compile/fail/ref=CheckNullCastable.out -XDrawDiagnostics CheckNullCastable.java
 */

__ByValue final class CheckNullCastable {
    void foo(CheckNullCastable cnc) {
        CheckNullCastable cncl = (CheckNullCastable) null;
        if (cnc != null) {};
        if (null != cnc) {};
    }
}
