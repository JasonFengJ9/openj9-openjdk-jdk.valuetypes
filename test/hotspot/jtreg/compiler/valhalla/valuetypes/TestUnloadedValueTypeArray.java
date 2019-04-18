/*
 * Copyright (c) 2017, 2019, Red Hat, Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @bug 8182997 8214898
 * @library /test/lib
 * @summary Test the handling of Arrays of unloaded value classes.
 * @run main/othervm -XX:+EnableValhalla -Xcomp
 *        -XX:CompileCommand=compileonly,TestUnloadedValueTypeArray::test1
 *        -XX:CompileCommand=compileonly,TestUnloadedValueTypeArray::test2
 *        -XX:CompileCommand=compileonly,TestUnloadedValueTypeArray::test3
 *        -XX:CompileCommand=compileonly,TestUnloadedValueTypeArray::test4
 *        -XX:CompileCommand=compileonly,TestUnloadedValueTypeArray::test5
 *        -XX:CompileCommand=compileonly,TestUnloadedValueTypeArray::test6
 *      TestUnloadedValueTypeArray
 */

import jdk.test.lib.Asserts;

value final class MyValue {
    final int foo;

    private MyValue() {
        foo = 0x42;
    }
}

value final class MyValue2 {
    final int foo;

    public MyValue2(int n) {
        foo = n;
    }
}

value final class MyValue3 {
    final int foo;

    public MyValue3(int n) {
        foo = n;
    }
}

value final class MyValue4 {
    final int foo;

    public MyValue4(int n) {
        foo = n;
    }
}

value final class MyValue5 {
    final int foo;

    public MyValue5(int n) {
        foo = n;
    }
}

value final class MyValue6 {
    final int foo;

    public MyValue6(int n) {
        foo = n;
    }

    public MyValue6(MyValue6 v, MyValue6[] dummy) {
        foo = v.foo + 1;
    }
}

public class TestUnloadedValueTypeArray {

    static MyValue[] target() {
        return new MyValue[10];
    }

    static void test1() {
        target();
    }

    static int test2(MyValue2[] arr) {
        if (arr != null) {
            return arr[1].foo;
        } else {
            return 1234;
        }
    }

    static void test2_verifier() {
        int n = 50000;

        int m = 9999;
        for (int i=0; i<n; i++) {
            m = test2(null);
        }
        Asserts.assertEQ(m, 1234);

        MyValue2[] arr = new MyValue2[2];
        arr[1] = new MyValue2(5678);
        m = 9999;
        for (int i=0; i<n; i++) {
            m = test2(arr);
        }
        Asserts.assertEQ(m, 5678);
    }

    static void test3(MyValue3[] arr) {
        if (arr != null) {
            arr[1] = new MyValue3(2345);
        }
    }

    static void test3_verifier() {
        int n = 50000;

        for (int i=0; i<n; i++) {
            test3(null);
        }

        MyValue3[] arr = new MyValue3[2];
        for (int i=0; i<n; i++) {
            test3(arr);
        }
        Asserts.assertEQ(arr[1].foo, 2345);
    }

    static MyValue4[] test4(boolean b) {
        // range check elimination
        if (b) {
            MyValue4[] arr = new MyValue4[10];
            arr[1] = new MyValue4(2345);
            return arr;
        } else {
            return null;
        }
    }

    static void test4_verifier() {
        int n = 50000;

        for (int i=0; i<n; i++) {
            test4(false);
        }

        MyValue4[] arr = null;
        for (int i=0; i<n; i++) {
          arr = test4(true);
        }
        Asserts.assertEQ(arr[1].foo, 2345);
    }

    static Object[] test5(int n) {
        if (n == 0) {
            return null;
        } else if (n == 1) {
            MyValue5[] arr = new MyValue5[10];
            arr[1] = new MyValue5(12345);
            return arr;
        } else {
            MyValue5?[] arr = new MyValue5?[10];
            arr[1] = new MyValue5(22345);
            return arr;
        }
    }

    static void test5_verifier() {
        int n = 50000;

        for (int i=0; i<n; i++) {
            test5(0);
        }

        {
            MyValue5[] arr = null;
            for (int i=0; i<n; i++) {
                arr = (MyValue5[])test5(1);
            }
            Asserts.assertEQ(arr[1].foo, 12345);
        }
        {
            MyValue5?[] arr = null;
            for (int i=0; i<n; i++) {
                arr = (MyValue5?[])test5(2);
            }
            Asserts.assertEQ(arr[1].foo, 22345);
        }
    }

    static Object test6() {
        return new MyValue6(new MyValue6(123), null);
    }

    static void test6_verifier() {
        Object n = test6();
        Asserts.assertEQ(n.toString(), "[MyValue6 foo=124]");
    }

    static public void main(String[] args) {
        test1();
        test2_verifier();
        test3_verifier();
        test4_verifier();
        test5_verifier();
        test6_verifier();
    }
}
