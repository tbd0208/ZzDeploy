const Table = {
		
	eachRow : function(table,fn){
		var rows = table.rows;
		for(var ri=0,rl=rows.length;ri<rl;ri++) fn(rows[ri],ri);
	}
	,eachCell : function(table,fn){
		var rows = table.rows;
		for(var ri=0,rl=rows.length;ri<rl;ri++){
			var cells = rows[ri].cells;
			for(var ci=0,cl=cells.length;ci<cl;ci++) fn(cells[ci],ci);
		}
	}
	,eachCellIndexs : function(table,indexs,fn){
		var rows = table.rows;
		for(var ri=0,rl=rows.length;ri<rl;ri++){
			var cells = rows[ri].cells;
			for(var i=0,l=indexs.length;i<l;i++) fn(cells[indexs[i]],i);
		}
	}

	/**
	 * toggler(checkbox)를 사용하여 1개 이상의 컬럼을 toggle할 수 있음
	 */ 
	, toggleColumn : function (table,togglers,p){
		var headRow = table.rows[p.headRowIndex];
		if(!headRow) return;
		var headCells = headRow.cells;
		var indexBox = new Array(togglers.length);
		for(var i=0;i<togglers.length;i++){
			var t = togglers[i];
			var toggleColIndexs = new Array(0);
			for(var n=0,l=headCells.length;n<l;n++) if(p.checkTarget(t,headCells[n])) toggleColIndexs.push(n);
			if(toggleColIndexs.length===0) continue;
			t.toggleColIndexs = toggleColIndexs;
			Table.toggle(table,t.toggleColIndexs,t.checked);
		}
		
		$(togglers).change(function(){
			Table.toggle(table,this.toggleColIndexs,this.checked);
		});
	}

	,toggle : function(table,indexs,y){
		var rows = table.rows;
		for(var i=0;i<indexs.length;i++) for(var ri=0,rl=rows.length;ri<rl;ri++) p.toggle(rows[ri].cells[indexs[i]],y);
	}
	
	,currentColunmCopy : function(){
		var t = this;
		var td = $(t).parent();
		var tr = td.parent();
		var thead = tr.parent();
		alert(Table.getColumnTexts(thead.next()[0],td.index()));
	}
	
	,getColumnTexts : function (table,colIndex){
		var rows = table.rows;
		var texts = new Array(rows.length);
		for(var i=0;i<rows.length;i++){
			texts[i] = rows[i].cells[colIndex].innerText;
		}
		return texts.join(' ');
	}
	,getColumnTexts : function (table,colIndex){
		var texts = new Array(rows.length);
		Table.eachCellIndex(table,[colIndex],function(cell,i){
			texts[i] = cell.innerText;
		});
		return texts.join(' ');
		
		var rows = table.rows;
		for(var i=0;i<rows.length;i++){
			texts[i] = rows[i].cells[colIndex].innerText;
		}
		return texts.join(' ');
	}
}