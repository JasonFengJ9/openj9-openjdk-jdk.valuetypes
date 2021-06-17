/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

#import "GroupAccessibility.h"
#import "JNIUtilities.h"
#import "ThreadUtilities.h"
/*
 * This is the protocol for the components that contain children.
 * Basic logic of accessibilityChildren might be overridden in the specific implementing
 * classes reflecting the logic of the class.
 */
@implementation GroupAccessibility
- (NSAccessibilityRole _Nonnull)accessibilityRole
{
    return NSAccessibilityGroupRole;
}

/*
 * Return all non-ignored children.
 */
- (NSArray *)accessibilityChildren {
    JNIEnv *env = [ThreadUtilities getJNIEnv];

    NSArray *children = [JavaComponentAccessibility childrenOfParent:self
                                                             withEnv:env
                                                    withChildrenCode:JAVA_AX_ALL_CHILDREN
                                                        allowIgnored:NO];

    if ([children count] == 0) {
        return nil;
    } else {
        return children;
    }
}

@end
