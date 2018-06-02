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
package oracle.micro.valhalla.baseline.types;

import oracle.micro.valhalla.types.PNumber;

public final class Box2 implements PNumber {

    public final int f0;
    public final int f1;

    public Box2(int f0, int f1) {
        this.f0 = f0;
        this.f1 = f1;
    }

    public int f0() {
        return f0;
    }

    public int f1() {
        return f1;
    }

    public int re() {
        return f0;
    }

    public int im() {
        return f1;
    }

    public Box2 add(Box2 v) {
        return new Box2(this.f0 + v.f0, this.f1 + v.f1);
    }

    public Box2 mul(Box2 v) {
        int tre = this.f0;
        int tim = this.f1;
        int vre = v.f0;
        int vim = v.f1;
        return new Box2(tre * vre - tim * vim, tre * vim + vre * tim);
    }

    // Used to provide usages of both fields in bechmarks
    public int totalsum() {
        return f0 + f1;
    }

    @Override
    public int hashCode() {
        return f0 + f1;
    }

    @Override
    public Box2 inc() {
        return new Box2(f1, f0 + 1);
    }

    @Override
    public Box2 dec() {
        return new Box2(f1, f0 - 1);
    }

}
