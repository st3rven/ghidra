/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.program.emulation;

import ghidra.pcode.emulate.Emulate;
import ghidra.pcode.emulate.EmulateInstructionStateModifier;
import ghidra.pcode.emulate.callother.OpBehaviorOther;
import ghidra.pcode.memstate.MemoryState;
import ghidra.pcodeCPort.error.LowlevelError;
import ghidra.program.model.pcode.Varnode;

public class m68kEmulateInstructionStateModifier extends EmulateInstructionStateModifier {

/*
	private Register ismReg;
	private Register isaModeReg;
	private RegisterValue ISA_MODE0;
	private RegisterValue ISA_MODE1;
*/

	public m68kEmulateInstructionStateModifier(Emulate emu) {
		super(emu);
/*
		ismReg = language.getRegister("ISM");
		isaModeReg = language.getRegister("ISA_MODE");
		if (ismReg == null || isaModeReg == null) {
			throw new RuntimeException("Expected language " + language.getLanguageID() +
				" to have ISM and ISA_MODE registers defined");
		}
		ISA_MODE1 = new RegisterValue(isaModeReg, BigInteger.ONE);
		ISA_MODE0 = new RegisterValue(isaModeReg, BigInteger.ZERO);
*/

		//registerPcodeOpBehavior("findFirstOne", new FindFirstOneOpBehavior());
	}

	/**
	 * Initialize ISM register based upon context-register state before first
	 * instruction is executed.
	 */
/*
    @Override
    public void initialExecuteCallback(Emulate emulate, Address current_address, RegisterValue contextRegisterValue) throws LowlevelError {
            BigInteger isaModeValue = BigInteger.ZERO;
            if (contextRegisterValue != null) {
                    isaModeValue = contextRegisterValue.getRegisterValue(isaModeReg).getUnsignedValueIgnoreMask();
            }
            if (!BigInteger.ZERO.equals(isaModeValue)) {
                    isaModeValue = BigInteger.ONE;
            }
            emu.getMemoryState().setValue(ismReg, isaModeValue);
    }
*/

	/**
	 * Use ISM register value to establish ISA_MODE when branching/calling. If
	 * ISM = 0, check for odd destination address which may occur when
	 * jumping/returning indirectly to Thumb mode. It is assumed that language
	 * will properly handle context changes during the flow of execution, we
	 * need only fix the current program counter.
	 */
/*
    @Override
    public void postExecuteCallback(Emulate emulate, Address lastExecuteAddress,
                    PcodeOp[] lastExecutePcode, int lastPcodeIndex, Address currentAddress)
                    throws LowlevelError {
            if (lastPcodeIndex < 0) {
                    // ignore fall-through condition
                    return;
            }
            int lastOp = lastExecutePcode[lastPcodeIndex].getOpcode();
            if (lastOp != PcodeOp.BRANCH && lastOp != PcodeOp.CBRANCH && lastOp != PcodeOp.BRANCHIND &&
                    lastOp != PcodeOp.CALL && lastOp != PcodeOp.CALLIND && lastOp != PcodeOp.RETURN) {
                    // only concerned with Branch, Call or Return ops
                    return;
            }
            long tbValue = emu.getMemoryState().getValue(ismReg);
            if (tbValue == 1) {
                    // Thumb mode
                    emu.setContextRegisterValue(ISA_MODE1); // change context to be consistent with ISM value
                    if ((currentAddress.getOffset() & 0x1) == 1) {
                            emulate.setExecuteAddress(currentAddress.previous());
                    }
            }
            else {

                    if ((currentAddress.getOffset() & 0x1) == 1) {
                            throw new LowlevelError(
                                    "Flow to odd address occurred without setting ISM register (16-bit mode)");
                    }

                    // MIPS mode
                    emu.setContextRegisterValue(ISA_MODE0); // change context to be consistent with ISM value
            }
    }
*/

	private static class FindFirstOneOpBehavior implements OpBehaviorOther {

		@Override
		public void evaluate(Emulate emu, Varnode out, Varnode[] inputs) {
			if (out == null) {
				throw new LowlevelError("CALLOTHER: Find First One op missing required output");
			}

			if (inputs.length != 2 || inputs[1].getSize() == 0 || !inputs[1].isRegister()) {
				throw new LowlevelError(
					"CALLOTHER: Find First One op requires one register varnode input");
			}

			Varnode in = inputs[1];
			MemoryState memoryState = emu.getMemoryState();

			long value = memoryState.getValue(in);
			long size = in.getSize() * 8;
			long count = size - 1;
			long mask = 1L << count;
			while ((count >= 0) && ((mask & value) == 0)) {
				--count;
				value = value << 1;
			}

			memoryState.setValue(out, count >= 0 ? count : size);
		}

	}
}
