(function ($) {
	
	$.tabBind = function(p) {
		return function(p){
			
			p = $.extend($.tabBind.setting = {
				
				label : 'NO-LABEL',
					
				tabHeadBoxClass : 'tabHeadBox',
				tabBodyBoxClass : 'tabBodyBox',
				tabHeadClass : 'tabHead',
				tabBodyClass : 'tabBody',
				onTabClass:'on',
				loadTabClass:'tab-load',
				
				initTab : null,
				initTabIndex : 0,
				
				cookie : null,
				
				target : null,
				head : null,
				body : null,
				headBox : null,
				bodyBox : null,
				heads : null,
				bodys : null
				
				,tabHeadFilter : ''
								
				,onTabAfter : null
				,oneTabAfter : null
			},p);
			
			if(console) console.info(p.label);
			
			var head,body;
			
			var a = p.target;
			if(a){
				var type = typeof(a);
				var subBoxs = null;
				switch(type){
				case "object" :
					subBoxs = $(a).children();
					break;
				default : 
					subBoxs = $("#"+a).children();
					if(p.cookie!==false) p.cookie = a;
					break;
				}
				head = subBoxs.eq(0);
				body = subBoxs.eq(1);
			}else{
				head = toIdToObject(p.head||p.tabHead||p.headBox);
				body = toIdToObject(p.body||p.tabBody||p.bodyBox);
			}
			
			function toIdToObject(t){
				var type = typeof(t);
				switch(type){
				case "string" : return $("#"+t);
				case "object" : return t;
				default : alert('toIdToObject : ' + type); break; 
				}
				return t;
			}
						
			var tabHeads = head.children();
			var tabBodys = body.children();
			var bIndex=-1;
			
			if(p.tabHeadFilter) tabHeads = tabHeads.find(p.tabHeadFilter);
			
			$(head).addClass(p.tabHeadBoxClass);
			$(body).addClass(p.tabBodyBoxClass);
			tabHeads.addClass(p.tabHeadClass);
			tabBodys.addClass(p.tabBodyClass);
			
			tabHeads.one('click',function(){$(this).addClass(p.loadTabClass);});
			tabHeads.on("click",function(){
				if(bIndex>-1 && tabHeads[bIndex]===this) {
					if(p.closeTab){
						tabHeads.eq(bIndex).removeClass(p.onTabClass);
						tabBodys.eq(bIndex).removeClass(p.onTabClass);
						if(p.onTabAfter) p.onTabAfter(bIndex,this,tabBodys.eq(bIndex)[0]);
						bIndex = -1;
					}
					return;
				}
				
				var cIndex = tabHeads.index(this);
				if(p.onTabBefore) p.onTabBefore(cIndex,this,tabBodys.eq(cIndex)[0]);
				
				$(this).addClass(p.onTabClass);
				tabBodys.eq(cIndex).addClass(p.onTabClass);
				
				if(p.onTabAfter) p.onTabAfter(cIndex,this,tabBodys.eq(cIndex)[0]);
				
				if(bIndex>-1){
					tabHeads.eq(bIndex).removeClass(p.onTabClass);
					tabBodys.eq(bIndex).removeClass(p.onTabClass);
				}
				
				bIndex = cIndex;
//				this.index = bIndex;
				if(p.cookie) $.cookie(p.cookie,cIndex);
			});
			
			if(p.oneTabAfter) tabHeads.one("click",function(){
				var cIndex = tabHeads.index(this);
				p.oneTabAfter(cIndex,this,tabBodys.eq(cIndex)[0]);
			});
			
			if(p.cookie) tabHeads.eq($.cookie(p.cookie)).click();
			else if(tabHeads.hasClass(p.onTabClass)) $('.'+p.onTabClass,tabHeads.parent()).click();
			else if(p.initTabIndex!==undefined){
				if(p.initTabIndex!==null) tabHeads.eq(p.initTabIndex=='last'?tabHeads.length-1:p.initTabIndex).click();
			}
			else if(p.initTab) p.initTab.click();
//			else tabHeads.eq(0).click();
			
			this.append = function(i,t){
				if(tabBodys.length) tabBodys.eq(i-1).after(t);
				else tabBody.append(t);
				tabBodys = tabBody.children();
			};
		}(p)
	};
})(jQuery);