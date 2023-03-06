String.prototype.padding = function(str,len){return new Array(1+len-this.length).join(str) + this;};
String.prototype.string = function(len){var s = '', i = 0; while (i++ < len) { s += this; } return s;};
String.prototype.zf = function(len){return "0".string(len - this.length) + this;};
if(!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position){
      position = position || 0;
      return this.substr(position, searchString.length) === searchString;
  };
}
String.prototype.mapping = function(map) {
	return this.replace(/#{(\S+)}/g, function(match,key) { 
		return typeof map[key]==='undefined'?key:map[key];
	});
};
String.prototype.format = function() {
	var arg = arguments;
	return this.replace(/{(\d+)}/g, function(match,d) { 
      return arg[d];
    });
};
String.prototype.endsWith = function(str){
	if (this.length < str.length) return false;
	return this.lastIndexOf(str) + str.length == this.length;
}
String.prototype.offNumberFormat = function(){
	return (this).replace(/[^\d-]+/g,"");
}
String.prototype.onNumberFormat = function(){
	return (this).replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
}

String.prototype.number = function(){
	return (this).replace(/[^\d-]+/g,"");
}
String.prototype.money = function(){
	return (this).replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
}
String.prototype.toInt = function(){
	return parseInt(this.number()||0);
}
String.prototype.isDigit = function(){
	return /^\d+$/.test(this.trim());
}

Number.prototype.zf = function(len){return this.toString().zf(len);};
Number.prototype.cut = function(n){
	return Math.floor(this/n)*n;
}
Number.prototype.floor = function(n){
	n = Math.pow(10,n);
	return Math.floor(this/n)*n;
}
Number.prototype.round = function(n){
	n = Math.pow(10,n);
	return (Math.round(this/n)*n).toFixed(n);
}

Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";
 
    var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var d = this;
     
    return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy": return d.getFullYear();
            case "yy": return (d.getFullYear() % 1000).zf(2);
            case "MM": return (d.getMonth() + 1).zf(2);
            case "dd": return d.getDate().zf(2);
            case "E": return weekName[d.getDay()];
            case "HH": return d.getHours().zf(2);
            case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
            case "mm": return d.getMinutes().zf(2);
            case "ss": return d.getSeconds().zf(2);
            case "a/p": return d.getHours() < 12 ? "오전" : "오후";
            default: return $1;
        }
    });
};
Date.prototype.addDays = function(days){
	this.setTime(this.getTime()+(days*24*60*60*1000));
	return this;
};
Date.prototype.addMonths = function(months){
//	var d = this.getDate();
	this.setMonth(this.getMonth()+months);
//	this.setDate(d);
	return this;
};
Date.prototype.addYears = function(year){
	this.setFullYear(this.getFullYear()+year);
	return this;
};
Date.prototype.yyyyMMdd = function(v){
	if(v){
		var date = parseInt(v);
		this.setFullYear(v/10000,date%10000/100-1,date%100);
		return this;
	}else{
		return this.format('yyyyMMdd');
	}
};
Date.dateTime = function(){
	return new Date().yyyyMMdd()+" "+new Time().format();
}

function Time(v){
	if(v===undefined){
		var date = new Date();
		this.h = date.getHours();
		this.m = date.getMinutes();
	}else{
		this.set(v);
	}
}
Time.prototype.set = function(v){
	v = v.zf(4);
	this.h = parseInt(v.substring(0,2));
	this.m = parseInt(v.substring(2));
	if(this.h > 23) this.h = 0;
	if(this.m > 59) this.m = 0;
	return this;
}
Time.prototype.addHs = function(addHs){
	var v = this.h;
	v+=addHs;
	if(v<0) v = 24+v;
	else if(v>=24) v = v-24;
	this.h = v;
	return this;
}
Time.prototype.addMs = function(addMs){
	var v = this.m;
	v+=addMs;
	if(v<0){
		v = 60+v;
		this.addHs(-1)
	}else if(v>=60){
		v = v-60;
		this.addHs(+1)
	}
	this.m = v;
	return this;
}
Time.prototype.toString = function(){
	return this.h.zf(2)+this.m.zf(2);
}
Time.prototype.format = function(){
	return this.h.zf(2)+":"+this.m.zf(2);
}

