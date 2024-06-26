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
package ghidra.app.plugin.core.symtable;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import docking.dnd.GTableDragProvider;
import docking.widgets.table.GTable;
import docking.widgets.table.RowObjectTableModel;
import ghidra.app.plugin.core.symtable.dnd.SymbolTransferable;
import ghidra.program.model.symbol.Symbol;

/**
 * A class that provides the ability to start the dragging of {@link Symbol}s.
 */
public class SymbolTableDragProvider extends GTableDragProvider<SymbolRowObject> {

	public SymbolTableDragProvider(GTable table, RowObjectTableModel<SymbolRowObject> model) {
		super(table, model);
	}

	@Override
	protected Transferable createDragTransferable(List<SymbolRowObject> items) {
		List<Symbol> symbols = new ArrayList<>();
		for (SymbolRowObject ro : items) {
			symbols.add(ro.getSymbol());
		}
		return new SymbolTransferable(table, symbols);
	}

}
