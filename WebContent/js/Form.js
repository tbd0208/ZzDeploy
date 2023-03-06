const Form = {
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
	
	,bindCookie : function(t){
		if(t.type==='radio' ){
			var es = t.form[t.name];
			if(es===t || es[0]===t){
				$(es).change(function(){
					if(this.checked) $.cookie(this.name,this.value);
				});
				var v = $.cookie(t.name);
				if(v) $(es).filter('[value='+v+']')[0].checked = true;
			}
		}else if(t.type==='checkbox'){
			$(t).change(function(){
				$.cookie(this.id||this.name,this.checked?1:0);
			});
			t.checked = 1==$.cookie(t.id||t.name);
			$(t).change();
		}else{
			$(t).change(function(){
				$.cookie(this.id||this.name,this.value);
			});
			var v = $.cookie(t.name);
			if(v!==undefined && v!=='null') t.value = v; 
		}
	}
}