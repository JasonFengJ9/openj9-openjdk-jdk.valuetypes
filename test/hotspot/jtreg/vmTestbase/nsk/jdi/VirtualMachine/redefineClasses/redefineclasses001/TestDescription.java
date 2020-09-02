/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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


/*
 * @test
 *
 * @summary converted from VM Testbase nsk/jdi/VirtualMachine/redefineClasses/redefineclasses001.
 * VM Testbase keywords: [quick, jpda, jdi, redefine]
 * VM Testbase readme:
 * DESCRIPTION:
 *     The test for the implementation of an object of the type
 *     VirtualMachine.
 *     The test checks up that a result of the method
 *     com.sun.jdi.VirtualMachine.redefineClasses()
 *     complies with its spec:
 *     public void redefineClasses(Map classToBytes)
 *      All classes given are redefined according to the definitions supplied.
 *      If any redefined methods have active stack frames, those
 *      active frames continue to run the bytecodes of the previous method.
 *      The redefined methods will be used on new invokes.
 *      If resetting these frames is desired, use ThreadReference.popFrames(StackFrame)
 *      with Method.isObsolete().
 *      This function does not cause any initialization except that which
 *      would occur under the customary JVM semantics. In other words,
 *      redefining a class does not cause its initializers to be run.
 *      The values of preexisting static variables will remain as they were
 *      prior to the call. However, completely uninitialized (new) static variables
 *      will be assigned their default value.
 *      If a redefined class has instances then all those instances will have
 *      the fields defined by the redefined class at the completion of the call.
 *      Preexisting fields will retain their previous values.
 *      Any new fields will have their default values;
 *      no instance initializers or constructors are run.
 *      Threads need not be suspended.
 *      No events are generated by this function.
 *      Not all target virtual machines support this operation.
 *      Use canRedefineClasses() to determine if the operation is supported.
 *      Use canAddMethod() to determine if the redefinition can add methods.
 *      Use canUnrestrictedlyRedefineClasses() to determine if
 *      the redefinition can change the schema, delete methods,
 *      change the class hierarchy, etc.
 *      Parameters:
 *          classToBytes - A map from ReferenceType to array of byte.
 *          The bytes represent the new class definition and
 *          are in Java Virtual Machine class file format.
 *      Throws:
 *          UnsupportedOperationException - if the target virtual machine does not
 *               support this operation.
 *               If canRedefineClasses() is false
 *                  any call of this method will throw this exception.
 *               If canAddMethod() is false attempting to add a method
 *                   will throw this exception.
 *               If canUnrestrictedlyRedefineClasses() is false,
 *               attempting any of the following will throw this exception
 *                   changing the schema (the fields)
 *                   changing the hierarchy (subclasses, interfaces)
 *                   deleting a method
 *                   changing class modifiers
 *                   changing method modifiers
 *          NoClassDefFoundError - if the bytes don't correspond to the reference type
 *                                 (the names don't match).
 *          VerifyError - if a "verifier" detects that a class, though well formed,
 *                        contains an internal inconsistency or security problem.
 *          ClassFormatError - if the bytes do not represent a valid class.
 *          ClassCircularityError - if a circularity has been detected
 *                                  while initializing a class.
 *          UnsupportedClassVersionError - if the major and minor version numbers in bytes
 *                                         are not supported by the VM.
 *     The test checks up on the following assertion:
 *          If any redefined methods have active stack frames,
 *          those active frames continue to run the bytecodes of the previous method.
 *          The redefined methods will be used on new invokes.
 *     The test works as follows:
 *     The debugger program - nsk.jdi.VirtualMachine.redefineClasses.redefineclasses001;
 *     the debuggee program - nsk.jdi.VirtualMachine.redefineClasses.redefineclasses001a.
 *     Using nsk.jdi.share classes,
 *     the debugger gets the debuggee running on another JavaVM,
 *     creates the object debuggee.VM, and waits for VMStartEvent.
 *     Upon getting the debuggee VM started,
 *     the debugger calls corresponding debuggee.VM methods to get
 *     needed data and to perform checks.
 *     In case of error the test produces the return value 97 and
 *     a corresponding error message(s).
 *     Otherwise, the test is passed and produces
 *     the return value 95 and no message.
 * COMMENTS:
 *     The test suite contains
 *     the precompiled class file redefineclasses001b.klass .
 *     Its source file is redefineclasses001b.ja .
 *     Test was updated according to rfe:
 *     4691123 TEST: some jdi tests contain precompiled .klass files undes SCCS.
 *     redefineclasses001b.ja was moved into newclass directory and renamed
 *     to redefineclasses001b.java.
 *     The precompiled class file is created during test base build process.
 *
 * @library /vmTestbase
 *          /test/lib
 * @build nsk.jdi.VirtualMachine.redefineClasses.redefineclasses001
 *        nsk.jdi.VirtualMachine.redefineClasses.redefineclasses001a
 *
 * @comment compile newclassXX to bin/newclassXX
 *          with full debug info
 * @run driver nsk.share.ExtraClassesBuilder
 *      -g:lines,source,vars
 *      newclass
 *
 * @run main/othervm PropertyResolvingWrapper
 *      nsk.jdi.VirtualMachine.redefineClasses.redefineclasses001
 *      ./bin
 *      -verbose
 *      -arch=${os.family}-${os.simpleArch}
 *      -waittime=5
 *      -debugee.vmkind=java
 *      -transport.address=dynamic
 *      "-debugee.vmkeys=${test.vm.opts} ${test.java.opts}"
 */

