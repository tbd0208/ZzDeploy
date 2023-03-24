const Ajax = {
	get : function(url,param){
		var v;
		$.ajaxSetup({async:false});
		$.get(url,param,r=>{v=r}).fail(this.fail);
		return v;
	},
	fail : function(r,b,c,d){
		try{
			throw b+','+c;
		}catch(e){
			alert(e);
			throw e
		}
	}
}