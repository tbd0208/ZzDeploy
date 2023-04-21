
new Vue({el: "#JEUS",data : {tab : null}});

new Vue({
	data(){ return {
		a : 1
	}}
	
	,computed : {
		c1 : ()=>'c1'
	}
	
	,created() {
//		console.info(this.$data);
//		console.info(this.a);
//		console.info(this.$computed);
//		console.info(this.c1);
//		console.info("##### DEFAULT TEST");
	}
});

function qFetch(url,fn){
	fetch(url).then(r=>r.ok?r.json():-1).then(v=>v.state==='ok'?v.data:v).then(fn || console.info );
}

// Vue.component("cm-tab",{
// 	template:'#cmTab'
// 	,props : ['heads','bodys','tab'] 	
// });


Vue.component("ws-tab",{
	
	template:'#wsTab'
		
	,props : {
		tabs:Array,
		tabIndex:Number,
		pathType:String
	}

	,data(){ return {
		tab : null
	}}
	
	,computed : {
		bodys(){
			return this.tabs && new Array(this.tabs.length);	
		}
	}
	
	,watch : {
		tab(){
//			console.info('test : ' + this.tab);
		}
		,tabs(){
			var cTab = this.tab||0;
			this.tab = null;
			this.headClick(cTab);
		}
	}
	
	,methods : {
		headClick(i){
			if(this.bodys[i]) this.tab = i;
			else {
				
				if(app.tab===0){
					qFetch('getWorkingsetGroup.do?v='+this.tabs[i],r=>{
						this.bodys[i] = r;
						this.tab = i;
						
						// rendering after
						this.$nextTick(()=>{
							$.bindWorkingset($("#"+r[0].id).parent().parent());	
						});
					});
				}else if(app.tab===1){
					
					const f = document.forms.sourceMng;
					
					var param = 'path='+encodeURIComponent(this.tabs[i].path);
					if(f.limitDays) param+='&limitDays='+f.limitDays.value;
					param += '&extension='+ $(f.extension).get().map(e => e.checked ? e.value : null ).filter(Boolean).join(',',-1);
					
// 					'/projectFileMng/getRecentFiles.do?path='+this.tabs[i].localPath+"&limitDays="+app.limitDays
					qFetch('/projectFileMng/getRecentFiles.do'+(param===''?'':'?'+param),r=>{
						this.bodys[i] = r;
						this.tab = i;
						this.$nextTick(()=>{
							$.bindWorkingset($("#tabBox-AhRtc").parent().parent());	
						});
					});		
				}
			}
		}
		,getExtenstion : v => v&&v.substring(v.lastIndexOf("\.")+1)
		,getNameFirst :  v => v&&v.substring(0,v.indexOf(" ",1))
		,getNameLast : v => v&&v.substring(v.indexOf(" ",1)+1)
		,getUrlFirst : v => v&&v.substring(v.charAt(0)==="/"?1:0,v.indexOf("/",1))
		,getUrlLast : v => v&&v.substring(v.lastIndexOf("/")+1,v.length)
		,getPathFirst : (v,dChar) => v&&v.substring(v.charAt(0)===dChar?1:0,v.indexOf(dChar,1))
		,getPathLast : (v,dChar) => v&&v.substring(v.lastIndexOf(dChar)+1,v.length)
		,checkResOpen: path => path.endsWith('jsp') || path.endsWith('html') || path.endsWith('jpg') || path.endsWith('png') || path.endsWith('jpeg')
		,toDate : v => new Date(parseInt(v)).format('yyyyMMdd HH:mm:ss')
		,toSimpleDate : v => toSimpleDate(v)
		,toSimpleFileSize : v => toSimpleFileSize(v)
	}
});

/* ### vue */
var app = new Vue({
	el: "#sourceMng"
	,props : {
		
	}

	,created() {
	}
	
	,data: {
		PROJECT_INFO_MAP
		, DSTR_LEVELS
		, PROJECT_NAMES
				
		,distributeLevel : localStorage.getItem('distributeLevel')
		,project : localStorage.getItem('project')
//		,limitDays : localStorage.getItem('limitDays')||'3'
		
//		,extensions : ['jsp','js','css','html','etc']
//		,extension : $.cookie('extension') || [true,true]
		
		,tab : 0
		,tabBox01 : []
		,tabBox02 : []
		,pjTabBox : {}
	
	}
	,watch : {
//		distributeLevel(v,before){
//			localStorage.setItem('distributeLevel',v);
//		}
//		,fieldShow(v,before){
//			localStorage.setItem('project',v);
//		}
//		,limitDays(v,before){
//			localStorage.setItem('limitDays',v);
//		}
		
		/*, $data: {
			handler : function(...vs) {
				console.log(vs)
			}
			,deep: true
		}*/
	}
	
	,methods : {
		test(){
			console.info('test');	
		}
		,touchJsp(distributeLevel,project){
			alert(JSON.stringify(Ajax.get("/svrCmd/touch.do",{distributeLevel,project})));
		}
		,onTabByWorkingset(reloadable){
			if(reloadable===true || !this.tabBox01) fetch('getWorkingsetGroupNames.do').then(r=>r.ok?r.json():-1).then(v=>v.state==='ok'?v.data:v).then(data=>{
// 				console.info(data);
				this.tabBox01 = data;
			});
// 			tabBox01[0] = 'ws-tab';
			this.tab = 0;
		}
		,onTabByProject(x){
			const f = document.forms.sourceMng;
			
			/* [AhRtc] | WAS : /data/src/was/ | 192.168.1.150 | 172.30.0.8 | 172.30.0.10 | WEB : /data/src/web/ | 192.168.1.150 | 117.52.102.69 | 117.52.102.72 | */
			fetch('/projectFileMng/getFolders.do?path='+encodeURIComponent(x.localPath)+"&limitDays="+f.limitDays.value).then(r=>r.ok?r.json():-1).then(v=>v.state==='ok'?v.data:v).then(data=>{
// 				console.info(data);
				$("#projectAttrBx").text(x.simpleProjectInfo);
				this.tabBox02 = data;
				this.pjTabBox[x.name] = data;
				this.tab = 1;
			});
// 			this.tab = 1;
		}
		,getServerProjectNames(x){
			return x.stringServerProjectNames;
		}
		,reload(){
			qFetch('workingset_init.do',function(){
				app.onTabByWorkingset(true);
			});
		}
	}
	,emits:{submit:(payload)=>!!payload} // form events
	,expose:[] // methods
	
	// {{ data | filter }}
	,filters:{}
	
});

//function viewProjectAttr(v){
//	$("#projectAttrBx").text(v);
//}
function getTemplate(nm){
	return document.forms.template[nm].innerText;
}
$(function(){
	$(document.body).on('click','a.open',function(){
		var path;
		var projectName;
		var pi;
		var site = 'http://www.autoinside.co.kr';
		if(this.pathname.endsWith('.jsp')){
			path = this.pathname;
			projectName = path.substring(1,path.indexOf('/',1));
			pi = PROJECT_INFO_MAP[projectName];
			var viewResolver_prefix = '/WEB-INF/view/';
			switch(projectName){
			case 'AiMgr' : site='https://automgr.autoinside.co.kr';  break;
			case 'SCAR02' : site='http://scar00.autoinside.co.kr/';  break;
			}
			path = path.substring(projectName.length+pi.localWebFolder.length+viewResolver_prefix.length,path.length-3)+'do';
		}else{
			path = this.pathname.substring(1);
			projectName = path.substring(LOCAL_PROJECT_PRE_PATH.length,path.indexOf('/',LOCAL_PROJECT_PRE_PATH.length));
			pi = PROJECT_INFO_MAP[projectName];
			path = path.substring(pi.path.length+pi.localWebFolder.length);
		}
		window.open(site+path,'OPEN');
		return false;
	})
});

function toSimpleDate(v){
	// 1분:60 1시간:3600 하루:86400
	if(!v) return ;
	var date = new Date(parseInt(v));
	var now = new Date();
	var s = parseInt((now.getTime()-date.getTime())/1000);

	if(s>86400*30) return "<span class='dt_df'>"+date.format('yyyy-MM-dd')+"</span>"; // 30일 후
	if(s>86400) return "<span class='dt_dd'>"+parseInt(s/86400)+"일전"+"</span>";
	if(s>3600*8) return "<span class='dt_hh'>"+parseInt(s/3600)+"시간전"+"</span>";
	if(s>3600) return "<span class='dt_hh8'>"+parseInt(s/3600)+"시간전"+"</span>";
	if(s>60) return "<span class='dt_mm'>"+parseInt(s/60)+"분전"+"</span>";
	return "<span class='dt_ss'>"+s+"초전"+"</span>";
}
function toSimpleFileSize(v){
	if(v<1024*1024) return '1Mb';
	return parseInt(v/1024/1024)+'Mb';
}

