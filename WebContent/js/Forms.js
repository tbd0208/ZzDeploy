const Forms = {
	visitEsByTagType : function(f,p){
		var es = f.elements;
		for(var i=0,l=es.length;i<l;i++){
			var t = es[i];
			var f = p[t.getAttribute('type')||t.tagName.toLowerCase()];
			if(f) f(t);
		}
	}

	,esToQueryString : function(es){
		var r = this.getName(es[0])+"="+es[0].value;
		for(var i=1;i<es.length;i++) r+="&"+this.getName(es[i])+"="+es[i].value
		return r;
	}

	,getName : function(e){
		return (e.length>0?e[0]:e).name;
	}
	
	,bindStorage : (t) => Forms.__bindStorage(t, (k,v)=>localStorage.setItem(k,v), (k)=>localStorage.getItem(k))
	,bindCookie :  (t) => Forms.__bindStorage(t, (k,v)=>$.cookie(k,v), (k)=>$.cookie(k))
	,__bindStorage : (t,fnSave,fnGet) => {
		if(t.type==='radio' ){
			var es = t.form[t.name];
			if(es===t || es[0]===t){
				$(es).change(function(){
					if(this.checked) fnSave(this.name,this.value);
				});
				var v = fnGet(t.name);
				if(v) $(es).filter('[value='+v+']')[0].checked = true;
			}
		}else if(t.type==='checkbox'){
			if(t.id){
				$(t).change(function(){
					fnSave(this.id,this.checked?1:0);
				});
				t.checked = '1'===fnGet(t.id);
				if(t.checked) $(t).change();
				return ;
			}
			if(t.name){
				var es = t.form[t.name];
				if(es===t || es[0]===t){
					$(es).change(function(){
						fnSave(t.name,$(es).get().map( e => e.checked?e.value:null ).filter(Boolean).join(','));
					});
					var checked = fnGet(t.name)
					if(checked){
						checked = checked.split(',');
						es.forEach( e => {
							for(var i=0;i<checked.length;i++){
								if(e.value===checked[i]){
									e.checked = true;
									$(e).change();
									return ;
								}
							}
						});
					}
				}
			}
		}else{
			$(t).change(function(){
				fnSave(this.id||this.name,this.value);
			});
			var v = fnGet(t.name);
			if(v!==undefined && v!=='null') t.value = v; 
		}
	}
}