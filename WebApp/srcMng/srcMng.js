$(function(){
	
	var pathType = 'normal'; // send file path structure type : normal, workingset
	
	const f = document.forms.sourceMng;
	const topMenu = $('#topMenu'); 
	const head00 = $('#tabHead-00');
	const groups = $('#tabBody-00').children();
	
	var workingsetHeads = new Array();
	var workingsetDatas = new Array();
	var lastWorkinsetData = null;
	
	topMenu.on('click','.menu-reload',e=>{
		head00.find('.on .reload').click();
	})
	;
	
	$('#toggleTableColBox input').change(e=>{
		const checked = e.target.checked;
		const toggleValue = $(e.target).parent().text();
		groups.find('.workingsetTable').each((i,table)=>{
			if(table.rows.length===0) return true;
			const headCells = table.rows[0].cells;
			const toggleColIndexs = new Array(0);
			for(var n=0,l=headCells.length;n<l;n++){
				var cell = headCells[n];
				if((cell.getAttribute('togglecolumnname')||cell.innerText)===toggleValue) toggleColIndexs.push(n);
			}
			if(toggleColIndexs.length>0){
				Table.eachCellIndexs(table,toggleColIndexs,function(cell,i){
					cell.classList.toggle('none',!checked);
				});
			}
		});
	});
	
	groups
	// 그룹 파일 업로드
	.on('click','.btnUploadAll',e=>{
		var head =$(e.target).closest('thead'); 
		if(!confirm("["+f.distributeLevel.value+"] Upload 하시겠습니까?\n" +  head.find('.workingsetName').text())) return ;
		upload(head);
	})
	// 단일 파일 업로드
	.on('click',".oneUploadBtn",e=>{ 
		if(!confirm("["+f.distributeLevel.value+"] Upload 하시겠습니까?\n"+$(e.target).closest("tr").data("path"))) return ;
		upload($(e.target).closest('tbody').prev(),$(e.target).closest('tr').index());
	})
	// 그룹 COPY파일 다운로드
	.on('click','.btnDownFormServerAll',e=>{
		var head =$(e.target).closest('thead'); 
		if(!confirm("서버 COPY본을 만드시겠습니까?" +  head.find('.workingsetName').text())) return ;
		head.next().children().find(".btnDownFormServer").each(onDownFromServer);
	})
	// 단일 COPY파일 다운로드
	.on('click','.btnDownFormServer',e=>{
		if(!confirm("서버 COPY본을 만드시겠습니까?"+$(e.target).closest("tr").data("path"))) return ;
		onDownFromServer.call(e.target);
	})
	// 그룹 파일 백업
	.on('click','.btnBackupAll',function(e){
		var head =$(e.target).closest('thead');
		if(!confirm("서버 백업본을 만드시겠습니까?")) return ;
		head.next().children().find(".btnBackup").each(onMakeBackupFile);
	})
	// 단일 파일 백업
	.on('click','.btnBackup',function(e){
		if(!confirm("서버 백업본을 만드시겠습니까?")) return ;
		onMakeBackupFile.call(e.target);
	})
	// 그룹 토글
	.on('click','.toggleWorkingSet',e=>{
		var $t = $(e.target);
		var tg = $t.text()!=='■';
		var $head = $t.text(tg?'■':'□').closest('.workingSet_head');
		$head.next().toggle(tg);
		$head.toggleClass('off',tg);
	})
	// 페이지 바로가기
	.on('click','a.open',e=>{
		var path;
		var projectName;
		var pi;
		var site = 'http://www.autoinside.co.kr';
		if(this.pathname.endsWith('.jsp')){
			path = e.target.pathname;
			projectName = path.substring(1,path.indexOf('/',1));
			pi = PROJECT_INFO_MAP[projectName];
			var viewResolver_prefix = '/WEB-INF/view/';
			switch(projectName){
			case 'AiMgr' : site='https://automgr.autoinside.co.kr';  break;
			case 'SCAR02' : site='http://scar00.autoinside.co.kr/';  break;
			}
			path = path.substring(projectName.length+pi.localWebFolder.length+viewResolver_prefix.length,path.length-3)+'do';
		}else{
			path = e.target.pathname.substring(1);
			projectName = path.substring(LOCAL_PROJECT_PRE_PATH.length,path.indexOf('/',LOCAL_PROJECT_PRE_PATH.length));
			pi = PROJECT_INFO_MAP[projectName];
			path = path.substring(pi.path.length+pi.localWebFolder.length);
		}
		window.open(site+path,'OPEN');
		return false;
	})
	;
	
	function onDownFromServer(){
		var t = $(this);
		var tr = t.closest('tr');
		var path = tr.data('path');
		var distributeLevel = f.distributeLevel.value;
		// if(lvl==='ALL') lvl = 'OPE';
		$.get("downFromServer.do",{path,distributeLevel},function(data){
			tr.children().last().text(data + new Date().format('HH:mm:ss'));
		});
	}
	function onMakeBackupFile(){
		var t = $(this);
		var tr = t.closest('tr');
		var path = tr.data('path');
		$.get("makeBackupFile.do",{path},function(data){
			tr.children().last().text(data + new Date().format('HH:mm:ss'));
		});
	}
	function upload(workingsetHead,childrenIndex){
		var wData = getWorkingsetData(workingsetHead);
		if(lastWorkinsetData) for(var i=0,l=lastWorkinsetData.children.length;i<l;i++) lastWorkinsetData.children[i].extension.addClass('off');
		
		lastWorkinsetData = wData;
		
		var sendData = {
			// pathType : pathType,
			distributeLevel:$('input[name=distributeLevel]:checked').val(),
			group:wData.name,
			hasUpload:$("#hasUpload")[0].checked,
			hasBackup:$("#hasBackup")[0].checked,
			makeDirectory:$("#makeDirectory")[0].checked,
			path:null
		};
		
		var fmtDate = new Date().format('HH:mm:ss');
		var children = childrenIndex===undefined?wData.children:[wData.children[childrenIndex]];
		for(var i=0,l=children.length;i<l;i++){
			var c = children[i];
			sendData.path = c.path;

			var resMsg = Ajax.get("upload.do",sendData);
			if(resMsg.state!=='ok'){
				c.t.find('.result').text(resMsg.msg)	
				break;
			}
			
			var data = resMsg.data;
			if(data.checkJavaCompile === false){
				c.t.find('.result').text(fmtDate+" compile : " + data.checkJavaCompile)
				break;
			}
			
			var distributeLevels = data.distributeLevels;
			var sendMsg = "";
			var dtlMsg = "";
			for(var d in distributeLevels){
//					var backupMsg = "";
				var backupPath = "";
				var dres = distributeLevels[d];
				sendMsg += "[" + d + ":";
				dtlMsg += "  ["+d+"] : " +dres.ftpPath + "\n"
				for(var serverNm in dres.servers){
					sendMsg += "" + dres.servers[serverNm].sendMsg;
//							backupMsg += "" + upload[uploadSvr].backupMsg;
					var tmp = dres.servers[serverNm].backupPath;
					if(tmp) backupPath+='  [BAK] : '+tmp+'\n'
				}
				sendMsg += "]"
				;
			}
			c.t.find('.result').text(fmtDate+" "+sendMsg);
			$("#uploadRes").html(function(){
				return "# "+fmtDate+" "+sendMsg + '\n  [LOC] : ' + sendData.path + '\n' + dtlMsg + backupPath + '\n' + this.innerHTML;
			});
		}
		function getWorkingsetData(workingsetHead){
			// var index = workingsetHeads.index(workingsetHead);
			// return workingsetDatas[index] || makeWorkingsetData(index,workingsetHead);
			// return makeWorkingsetData(index,workingsetHead);
			return makeWorkingsetData(0,workingsetHead);
			
			function makeWorkingsetData(index,workingsetHead){
				var name = workingsetHead.children().eq(0).find('.workingsetName').text();
				var workingSetBody = $(workingsetHead).next();
				var children = workingSetBody.children();
				return (workingsetDatas[index] = {
					name : name,
					children : $.map(children,function(t,i){
						return {
							t : $(t),
							extension : $(t).find('.extension'),
							path : $(t).data('path')
						}
					})
				});
			}
		}
	}
	
	
	
//	$(document.body)
//		.on('click',".extension",function(){
//			$(this).toggleClass('off');
//		})
//		.on('click',".selectExtensioBtnnBox",function(){
//			var v = $(this).text();
//			$(this).closest('thead').next().find(".extension_"+v+":first-child").click();
//		})
//		$('.selectExtensioBtnnBox>*').click(function(e){
//			var v = $(this).text();
//			$(this).closest('thead').next().find(">*>*:first-child.extension_"+v).click();
//		});
//		.on('click',".checkBoxAll",function(){
//			var tg = $(this).text()!=='□';
//			$(this).text(tg?'□':'■').closest('thead').next('tbody').find('*>*:first-child').toggleClass('off',tg);
//		})
	;
		
	
	$.tabBind({target:'tabBox-00',cookie:true,oneTabAfter : function(i,tabHead,tabBody){
		if(!tabHead.getAttribute('tab-type')) return ;
		$.ajaxSetup({async: false});
		switch(i){
		case 0 : 
			/*ajaxTab({
				tabHead : tabHead
				,tabBody : tabBody
				,template : getTemplate('workingsetGroupNames')
				,dataLink : 'getWorkingsetGroupNames.do'
				,sub : {
					template : getTemplate('workingset')
					,dataLink : 'getWorkingsetGroup.do'
					,dataParam : function(p){ return {v:p.tabHead.innerText}; }
					,afterParsing : bindWorkingset
				}
				,reload : function(){
					$.getJSON('workingset_init.do');
					pathType = 'workingset';
				}
			});*/
		break;
		case 1 : break;
		/*case 1 : 
			ajaxTab({
				tabHead : tabHead
				,tabBody : tabBody
				,template : getTemplate('bookmarks')
				,dataLink : '/projectFileMng/getBookmarks.do'
				,sub : {
					template : getTemplate('getRecentFiles')
					,dataLink : '/projectFileMng/getRecentFiles.do'
					,dataParam : function(p){ return {path:p.tabHead.dataset.path};}
					,afterParsing : bindWorkingset
				}
				,reload : function(){
					pathType = 'normal';
					$(".quickBox").html('');
				}
			});
		 break;*/
		default : 
//			ajaxTab({
//				tabHead : tabHead
//				,tabBody : tabBody
//				,template : getTemplate('getFolders')
//				,dataLink : '/projectFileMng/getFolders.do'
//				,dataParam : function(p){ return {path:p.tabHead.dataset.path,limitDays:$("#limitDays").val()};}
//				,sub : {
//					template : getTemplate('getRecentFiles')
//					,dataLink : function(){
//						var param = '';
//						var limitDays = $("#limitDays").val();
//						if(limitDays) param+='limitDays='+limitDays;
//						param += '&extension='+ $(f.extension).get().map(e => e.checked ? e.value : null ).join(',',-1);
//						
//						return '/projectFileMng/getRecentFiles.do'+(param===''?'':'?'+param);
//					}
//					,dataParam : function(p){ return {path:p.tabHead.dataset.path};}
//					,afterParsing : bindWorkingset
//				}
//				,reload : function(){
////					pathType = 'normal';
//				}
//			});
		
		 break;
		}
	}});
		
		
		
		// ### BIND WORKING SET
		function bindWorkingset(t){
			
			var table = $("table",t)[0];
			if(table.rows.length===0) return ;
			
//			var togglers = $('input','#toggleTableColBox');
//			var headCells = table.rows[0].cells;
//			
//			for(var i=0;i<togglers.length;i++){
//				var toggler = togglers[i];
//				var ckName = $(toggler).parent().text();
//				var toggleColIndexs = new Array(0);
//				for(var n=0,l=headCells.length;n<l;n++){
//					var headCell = headCells[n];
//					if((headCell.getAttribute('toggleColumnName')||headCell.innerText)===ckName) toggleColIndexs.push(n);
//				}
//				if(toggleColIndexs.length===0) continue;
//				toggler.toggleColIndexs = toggleColIndexs;
//				toggle(table,toggler.toggleColIndexs,toggler.checked);
//				$(toggler).change(function(){
//					toggle(table,this.toggleColIndexs,this.checked);
//				});
//				function toggle(table,indexs,checked){
//					Table.eachCellIndexs(table,indexs,function(cell,i){
//						cell.classList.toggle('none',!checked);
//					});
//				}
//			}
			
			workingsetHeads = $("thead",t);
			workingsetDatas = [];
			
			// 워킹셋 해드
			$('thead',t)
//			.on('click','.btnUploadAll',function(e){ // 워킹셋 전체 업로드
//				if(!confirm("Upload 하시겠습니까?")) return ;
//				upload($(e.delegateTarget));
//			})
//			.on('click','.reload',function(e){ // 리로드
//				var head = $(this).closest('.workingSet_head');
//				var resMsg = Ajax.get("getWorkingset.do",{id:head[0].id});
//				if(resMsg.state==='ok'){
//					var tml = null;
//					
//					var body = head.next();
//					var ch00 = body.children();
//					var ch01 = resMsg.data.projectFiles;
//					var a=b=0;
//					
//					body.empty();
//					
//					LOOP : for(;b<ch01.length;b++){
//// 						var c1 = ch01[b], c0 = ch00[a];
//						var c1 = ch01[b];
//						for(a=0;a<ch00.length;a++){
//							var c0 = ch00[a];
//							if(c1.path === c0.title) {
//								var e = ch00.splice(a,1);
//								if($(e).hasClass('dis')) $(e).removeClass('dis').addClass('new');
//								body.append(e);
//								continue LOOP;
//							}
//						}
//						if(tml===null) tml = Handlebars.compile(document.forms.template.workingset_projectFile.innerText);
//						var tr = $(tml(c1));
//						body.append(tr);
//						$(".toggleTableCol","#toggleTableColBox").each(function(){
//							tr.children().eq(this.value).toggleClass('none',!this.checked).removeClass('dis');
//						});
//						tr.addClass('new');
//					}
//					
//					for(a=0;a<ch00.length;a++){
//						body.append($(ch00[a]).addClass('dis'));
//					}
//					
//					togglers.click();
//				}
//			})
			;
			
//			var wsbody = $('.workingSet_body',t);
//			var maxdt = null; 
//			var maxOpeUpDt = null;
//			var editSoruceLimitTime = new Date().getTime()-(24*60*60*1000*7); 
//			wsbody.find(".lastModified").each(function(){
//				var t = $(this);
//				var v = t.attr('v');
//				
//				// 마지막 수정항목 찾기
//				maxdt = Math.max(maxdt,v);
//				
//				// 최근7일 수정항목 중 비업로드된 내역 찾기
//				if(v>editSoruceLimitTime){
//					var tr = t.parent();
//					var opeUpVal = tr.find('.opeUploadTime').attr('v');
//					if(opeUpVal<v) tr.addClass('editSoruce');
//					maxOpeUpDt = Math.max(maxOpeUpDt,opeUpVal);
//				}
//			}).each(function(){
//				$(this).filter("[v="+maxdt+"]").addClass('last-of-lastModified');
//			});
//			wsbody.find(".opeUploadTime").filter("[v="+maxOpeUpDt+"]").addClass('last-of-lastModified');
//			
//			var t = wsbody.find('.on').parent().prev();
//			if(t.length>0) {
//				t.closest('.tabBodyBox').scrollTop(t.position().top);
//			}
			
			return t;
		}
		
		$.bindWorkingset = bindWorkingset;
		
		
	});


//function ajaxTab(p){
//	var tml = Handlebars.compile(p.template);
//	var fn = function(p){
//		$.getJSON(typeof p.dataLink==='function'?p.dataLink():p.dataLink,p.dataParam?p.dataParam(p):null,function(res){
//			var tb = $(p.tabBody).html(tml({data:res.data}));
//			if(p.afterParsing) p.afterParsing(tb);
//			if(p.sub){
//				var sp = p.sub;
//				sp.parent = p;
//				$.tabBind({target:p.tabBody,cookie:p.dataLink,oneTabAfter : function(i,tabHead,tabBody){
//					sp.tabHead = tabHead;
//					sp.tabBody = tabBody;
//					setTimeout(function(){
//						ajaxTab(sp);
//					});
//				}});
//			}
//		});
//	};
//	
//	if(p.reload){
//		p.reload();
//		$(p.tabHead).find('.reload').click(function(){
//			p.reload();
//			fn(p);
//		});
//	}
//	fn(p);
//}
//
//function ajaxTabSub(p,lvl){
//	lvl++;
//	var tml = Handlebars.compile(p.template);
//	var fn = function(p){
//		$.getJSON(p.dataLink,p.dataParam?p.dataParam(p):null,function(res){
//			var data = res.data;
//			var files = new Array(0);
//			var folders = new Array(0);
//			
//			for(var i=0,l=data.length;i<l;i++){
//				if(data[i].isDirectory) folders.push(data[i]);
//				else files.push(data[i]);
//			}
//			
//			if(folders.length){
//				var tabHead = $(p.tabHead);
//				var tabBody = $(p.tabBody);
////				for(var i=0,l=folders.length;i<l;i++){
////					var t = folders[i];
////					$('<div class="tabHead" data-path="'+t.path+'">'+t.name+'</div>').insertAfter(tabHead).css('text-indent',(lvl*16)+'px');
////					tabBody.after('<div></div>');
////				}
//			}
//			
//			if(files.length){
//				var tb = $(p.tabBody).html(tml({data:files}));
//				if(p.afterParsing) p.afterParsing(tb);
//				if(p.sub){
//					var sp = p.sub;
//					sp.parent = p;
//					$.directoryBind({target:p.tabBody,cookie:p.dataLink,oneTabAfter : function(tabHead,tabBody){
//						sp.tabHead = tabHead;
//						sp.tabBody = tabBody;
//						ajaxTabSub(sp,lvl);
//					}});
//				}
//			}
//		});
//	};
//	
//	if(p.reload){
//		p.reload();
//		$(p.tabHead).find('.reload').click(function(){
//			p.reload();
//			fn(p);
//		});
//	}
//	
//	fn(p);
//}



$(function(){
	
	var f = document.forms.sourceMng;
	
	Forms.visitEsByTagType(f,{
		text : function(t){
		},
		select : function(t){
			Forms.bindStorage(t);
		}, 
		checkbox : function(t){
			Forms.bindStorage(t);
			bindCheckboxCheckedLabel(t);
		},
		radio : function(t){
			Forms.bindStorage(t);
			bindRadioCheckedLabel(t);
		},
		button : function(t){
		}
	});
	
	function bindCheckboxCheckedLabel(t){
		var p = $(t).parent();
		if(p[0].tagName==='LABEL'){
			var checked = function(){
				if(this.checked) p.attr('checked','');
				else p.removeAttr('checked');
			}
			$(t).change(checked);
			if(t.checked) checked.call(t);
		};
	}
	function bindRadioCheckedLabel(t){
		var p = $(t).parent();
		if(p[0].tagName==='LABEL'){
			var checked = function(){
				if(this.checked) {
					p.attr('checked','');
					if(this.form[this.name].checkedElement) this.form[this.name].checkedElement.removeAttr('checked'); 
					this.form[this.name].checkedElement = p;
				}
			}
			$(t).change(checked);
			if(t.checked) checked.call(t);
		};
	}
});