/*
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
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
package oracle.micro.valhalla.baseline.search;

import oracle.micro.valhalla.SearchBase;
import oracle.micro.valhalla.baseline.types.Box8;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class Search8 extends SearchBase {

    @State(Scope.Thread)
    public static class StatePrimitive {
        int[] arr;

        @Setup
        public void setup() {
            baseSetup();
            arr = new int[size*8];
            for (int i = 0; i < arr.length; i+=8) {
                arr[i] = i;
                arr[i+1] = i;
                arr[i+2] = i;
                arr[i+3] = i;
                arr[i+4] = i;
                arr[i+5] = i;
                arr[i+6] = i;
                arr[i+7] = i;
            }
        }
    }

    private static int binarySearch(int[] a,  int key) {
        int low = 0;
        int high = a.length/8 - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid*8];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    @Benchmark
    @OperationsPerInvocation(OPS)
    public void primitive(StatePrimitive st, Blackhole bh) {
        for (int t : targets) {
            bh.consume(binarySearch(st.arr, t));
        }
    }

    @State(Scope.Thread)
    public static class StateBoxed {
        Box8[] arr;

        @Setup
        public void setup() {
            baseSetup();
            arr = new Box8[size];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new Box8(i, i, i, i, i, i, i, i);
            }
        }
    }

    private static int binarySearch(Box8[] a,  int key) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid].f0;

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }


    @Benchmark
    @OperationsPerInvocation(OPS)
    public void boxed(StateBoxed st, Blackhole bh) {
        for (int t : targets) {
            bh.consume(binarySearch(st.arr, t));
        }
    }


}
