Date.prototype.format = function(f) {
	var d = this;
	return f.replace(/(yyyy|yy|MM|dd|EE|E|tt|HH|hh|mm|ss|a\/p)/g, function($1) {
		switch($1){
		case "yyyy": return d.getFullYear();
		case "yy": return zf(d.getFullYear()%100);
		case "MM": return zf(d.getMonth()+1);
		case "dd": return zf(d.getDate());
		case "EE": return ["sun","mon","tues","wednes","thurs","fri","satur"][d.getDay()]+'day';
		case "E" : return ["sun","mon","tue","wed","thu","fri","sat"][d.getDay()];
		case "KK": return ["일","월","화","수","목","금","토"][d.getDay()]+'요일';
		case "K" : return ["일","월","화","수","목","금","토"][d.getDay()];
		case "HH": return zf(d.getHours());
		case "hh": return zf(d.getHours()%12||12);
		case "mm": return zf(d.getMinutes());
		case "ss": return zf(d.getSeconds());
		case "a/p": return d.getHours()<12?"오전":"오후";
		case "tt": return d.getHours()<12?"AM":"PM";
		default: return $1;
		}
	});
	function zf(v){return v<10?'0'+v:v;}
};