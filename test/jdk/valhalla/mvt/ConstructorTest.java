/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
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

import jdk.incubator.mvt.ValueType;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertTrue;

/*
 * @test
 * @run testng/othervm -XX:+EnableMVT -Dvalhalla.enablePoolPatches=true ConstructorTest
 */

@Test
public class ConstructorTest {

    static final ValueType<?> VT_Interval = ValueType.forClass(Interval.class);

    static final MethodHandle VT_constructor;
    static {
        try {
            MethodHandle mh = VT_Interval.findConstructor(MethodHandles.lookup(),
                                                          MethodType.methodType(void.class, int.class, int.class));
            VT_constructor = MethodHandles.filterReturnValue(mh, VT_Interval.box());
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    public void testConstructorWithValidArguments() throws Throwable {
        Interval i = (Interval) VT_constructor.invoke(1, 2);
        assertEquals(i.l, 1);
        assertEquals(i.u, 2);
    }

    public void testConstructorWithIllegalArguments() throws Throwable {
        try {
            Interval i = (Interval) VT_constructor.invoke(3, 1);
            fail();
        } catch (Throwable t) {
            assertTrue(IllegalArgumentException.class.isInstance(t));
        }
    }
}
