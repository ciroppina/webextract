function aggiornaCurrent(stringa) {
	//debug: alert(stringa);
	document.getElementById("reg:current").value = stringa;
}

function showEntity(start, end){
	//does not work with jQuery objects!
	var tx=document.getElementById("reg:textArea");
	var txValue = tx.value.toUpperCase();
	var entity=$(".current").val().toUpperCase();

	var sottostringa=txValue.substring(0, end+1);
	if (start-10 >= 0)
		var sottostringa2=txValue.substring(start-10, end+1);
	else
		var sottostringa2=txValue.substring(start-0, end+1);
	var trueStart=sottostringa.toUpperCase().lastIndexOf(entity);
	var trueEnd= trueStart+entity.length;
//	alert("\n|"+start+"|"+end+"|" +"\n|"+trueStart+"|"+trueEnd+"|" 
//		 +"\n|"+entity+"|" +"\n|"+sottostringa2+"|" );

	if(tx.setSelectionRange) {
		tx.focus();
		tx.setSelectionRange(trueStart, trueEnd);
	}
	else if (tx.createTextRange) {
		var range=tx.createTextRange();
		range.collapse(true);
		range.moveEnd('character', trueEnd);
		range.moveStart('character', trueStart);
		range.select();
	}

//OR:	setSelection(tx, trueStart, trueEnd);
}

function offsetToRangeCharacterMove(el, offset) {
    return offset - (el.value.slice(0, offset).split("\r\n").length - 1);
}

function setSelection(el, start, end) {
	if (typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
        el.selectionStart = start;
        el.selectionEnd = end;
    } else if (typeof el.createTextRange != "undefined") {
        var range = el.createTextRange();
        var startCharMove = offsetToRangeCharacterMove(el, start);
        range.collapse(true);
        if (start == end) {
            range.move("character", startCharMove);
        } else {
            range.moveEnd("character", offsetToRangeCharacterMove(el, end));
            range.moveStart("character", startCharMove);
        }
        range.select();
    }
}

function selectRangeRel(fieldID,start,end,focuse) {
	var tx=parent.paperino.document.getElementById(fieldID); //textArea in altro frame
	var sottostringa=tx.value.substring(0,end+20); //sottostringa da 0 alla fine del focus
	
//	alert(focuse); 			//debug
	
	var splitted= focuse.split(" ");  //splitting del focus(stringa relazione) in token
	var i= splitted.length;          
	
//	alert(i+" "+splitted[i-1]); //prendo ultimo token della focus
	
	var endFocus= splitted[i-1];

	var trueEnd=sottostringa.toUpperCase().lastIndexOf(endFocus);
//	var trueEnd=sottostringa.toUpperCase().lastIndexOf(focuse);
	
//	alert (trueEnd);
	var trueStart= trueEnd-focuse.length;
	
//	alert ("start "+trueStart+" end "+trueEnd+" length "+focuse.length );	
	
		var trueStart2=trueStart+2;
	
	if(tx.setSelectionRange){
		tx.focus();
		tx.setSelectionRange(trueStart2,trueEnd);
		
	}
	else if (tx.createTextRange) {
		var range=tx.createTextRange();
		range.collapse(true);
		range.moveEnd('character',trueEnd);
		range.moveStart('character',trueStart);
		range.select();
	}
}

function replaceApice(stringa){
	stringa.replace("'","");
	return stringa;
}
