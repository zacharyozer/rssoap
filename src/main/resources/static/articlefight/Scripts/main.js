/*

File: main.js
Version: 1.1

By Paul Groudas, Michael McGraw-Herdeg, and Zachary Ozer

Based on Google Fight Code by shrubbery (shrubbery@kzoomedia.com)
and Google Fight Graphics by backwards (backwards@kzoomedia.com)

Google Fight (widget) and all related code and graphics are
copyright 2005 softwareFTW and Kalamazoo Media Group, LLC,
except where explicitly noted.  All rights are reserved to
softwareFTW and Kalamazoo Media Group, LLC.

We violated the following license:
"Licence:
This software is provided free of charge for use as a
whole, unmodified package.  You may redistribute this
package, so long as the package is provided unmodified,
and is credited to softwareFTW and Kalamazoo Media Group,
LLC. You may not modify or re-use the provided code or
graphics in any other packages, except where explicitly
noted, or with written consent from softwareFTW AND
Kalamazoo Media Group, LLC.

This software is provided by softwareFTW on an "AS IS"
basis.  SOFTWAREFTW MAKES NO WARRANTIES, EXPRESS OR IMPLIED,
INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES OF NON-
INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
PURPOSE, REGARDING THE ENCLOSED SOFTWARE OR ITS USE AND 
OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.

IN NO EVENT SHALL SOFTWAREFTW OR KALAMAZOO MEDIA GROUP, LLC
BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR 
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) ARISING IN ANY 
WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR 
DISTRIBUTION OF THE ENCLOSED SOFTWARE, HOWEVER CAUSED AND 
WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), 
STRICT LIABILITY OR OTHERWISE, EVEN IF SOFTWAREFTW OR 
KALAMAZOO MEDIA GROUP, LLC HAS BEEN ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE."

We appropriated the javascript animation without proper license.
Since this is an academic project and not a commercial venture,
and since we do not anticipate this ever seeing the light of day,
we feel that obtaining appropriate license for this plugin is a
little bit of overkill. Also, we don't have time right now.
Should we anticipate any further use of either this application 
or this view, we will either obtain an appropriate license or 
rewrite the code ourselves. 

*/

var search1_xml = null;
var search2_xml = null;
var result1 = 0;
var result2 = 0;
var result1pix = 0;
var result2pix = 0;
var curpix1 = 16;
var curpix2 = 16;
var curnum1 = 0;
var curnum2 = 0;
var num1inc = 0;
var num2inc = 0;
var result1bar;
var result2bar;
var updating = false;
var flipShown = false;
var spinner = null;
var error = null;

var animation = {duration:0, starttime:0, to:1.0, now:0.0, from:0.0, firstElement:null, timer:null};

if (window.widget) {
	widget.onhide = onHide;
	widget.onshow = show;
}

function resetVars() {
	search1_xml = null;
	search2_xml = null;
	result1 = 0;
	result2 = 0;
	result1pix = 0;
	result2pix = 0;
	curpix1 = 16;
	curpix2 = 16;
	curnum1 = 0;
	curnum2 = 0;
	num1inc = 0;
	num2inc = 0;
	result1bar = null;
	result2bar = null;
	updating = false;
	if(spinner) {
		spinner.style.display = "none";
		spinner = null;
	}
}

function show () {
	
}

function validate (e) {
	var search1 = document.getElementById("search1");
	var search2 = document.getElementById("search2");
	if(e.keyCode == 13 || e.keyCode == 3) {
		if(!validate_field(search1.value)) search1.focus();
		else if(!validate_field(search2.value)) search2.focus();
		else doFight();
	}
}

function validate_field(value) {
	if(value == "" || value == null || value.charAt(0) == ' ') return false;
	else return true;
}

function doFight (temp1, temp2) {
	result1 = temp1;
	result2 = temp2;
	showResultsTemp();
	calcBars();

	/*var search1 = document.getElementById("search1");
	var search2 = document.getElementById("search2");
	if(!validate_field(search1.value)) {
		search1.focus();
		return;
	}
	else if(!validate_field(search2.value)) {
		search2.focus();
		return;
	}
	
	if(updating) {
		showError(false, "");
		updating = false;
		return;
	}
	updating = true;
	
	showDownload();
	search1_xml = new XMLHttpRequest();
	
	search1_xml.onload = function(e) {search1_loaded(e, search1_xml);}
	search1_xml.open("GET", "http://www.google.com/xhtml?q="+search1.value+"&site=search&hl=en&lr=&c2coff=1&safe=off&mrestrict=xhtml&start=1&sa=N");
	search1_xml.overrideMimeType("text/xml");
	search1_xml.setRequestHeader("Cache-Control", "no-cache");
	search1_xml.send(null);*/

}

function search1_loaded(e, request) {
	var search2 = document.getElementById("search2");
	var writein = document.getElementById("writein");
	
	search1_xml = null;
	if (request.status != 200) {
		showError(true, "Could not contact Google");
		return;
	}
	if (request.responseXML) {		
		var data = parseNumResults(request);
		if(data >= 0) {
			result1 = data;
		}
		else {
			showError(true, "Parser Error, please report");
			return;
		}
		
		search2_xml = new XMLHttpRequest();
		
		search2_xml.onload = function(e) {search2_loaded(e, search2_xml);}
		search2_xml.open("GET", "http://www.google.com/xhtml?q="+search2.value+"&site=search&hl=en&lr=&c2coff=1&safe=off&mrestrict=xhtml&start=1&sa=N");
		search2_xml.overrideMimeType("text/xml");
		search2_xml.setRequestHeader("Cache-Control", "no-cache");
		search2_xml.send(null);
	}
	else {
		showError(true, "Could not contact Google");
		return;
	}	
}

function search2_loaded(e, request) {
	var writein2 = document.getElementById("writein2");
	
	search2_xml = null;
	if (request.status != 200) {
		showError(true, "Could not contact Google");
		return;
	}
	if(request.responseXML) {
		var data = parseNumResults(request);
		if(data >= 0) {
			result2 = data;
		}
		else {
			showError(true, "Parser error, please report");
			return;
		}
		showResultsTemp();
	}
	else {
		showError(true, "Could not contact Google");
		return;
	}
}

function parseNumResults(request) {
	var result;
	if(request) {
		result = findChild(request.responseXML, 'html');
		if(!result) return null;
		result = findChild(result, 'body');
		resulttmp = result.firstChild.nextSibling.nextSibling.nextSibling.innerHTML;
		if(resulttmp.search("newspaper_sm.gif") != -1) {
			resulttmp = result.firstChild.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.innerHTML;
		}
		result = resulttmp;
		if(!result) return null;
		result = result.split("about");
		result = result[1].split(".");
		result = result[0].replace(/,/g, "");
		return parseInt(result, 10);
	}
	else return null;
}

function showResultsTemp() {
	calcBars();
	if(result1 == null) result1 = 0;
	if(result2 == null) result2 = 0;
	var contents = document.getElementById('content');
	while (contents.hasChildNodes()) {
		contents.removeChild(contents.firstChild);
	}
	
	result1bar = document.createElement ('div');
	result1bar.setAttribute ('id', 'result1bar');
	result1bar.style.height = curpix1+"px";
	contents.appendChild (result1bar);
	
	result2bar = document.createElement ('div');
	result2bar.setAttribute ('id', 'result2bar');
	result2bar.style.height = curpix2+"px";
	contents.appendChild (result2bar);
	
	num1inc = parseInt(result1 / result1pix);
	num2inc = parseInt(result2 / result2pix);
	
	animateBars();
}

function animateBars() {
	if(curpix1 < result1pix) {
		curpix1++;
		result1bar.style.height = curpix1+"px";
		curnum1 = Math.round((curpix1/result1pix)*result1);
		result1bar.innerHTML = addCommas(curnum1);
	}
	else if (curnum1 != result1) result1bar.innerHTML = addCommas(result1);
	if(curpix2 < result2pix) {
		curpix2++;
		result2bar.style.height = curpix2+"px";
		curnum2 = Math.round((curpix2/result2pix)*result2);
		result2bar.innerHTML = addCommas(curnum2);
	}
	else if (curnum2 != result2) result2bar.innerHTML = addCommas(result2);
	if(curpix1 < result1pix || curpix2 < result2pix) {
		setTimeout('animateBars()', 8);
	}
	else {
		result1bar.innerHTML = addCommas(result1);
		result2bar.innerHTML = addCommas(result2);
		spinner = document.getElementById("spinner");
		spinner.style.display = "none";
		
		resetVars();
	}
}

function addCommas(nStr)
{
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}

function calcBars() {
	var high;
	var ceiling = 0;
	if(result1 < result2) {
		high = result2;
	}
	else {
		high = result1;
	}
	
	while(ceiling < high) ceiling += 1;
	result1pix = parseInt((result1 / ceiling) * 500, 10);
	result2pix = parseInt((result2 / ceiling) * 500, 10);
	
	if(result1pix < 18) result1pix = 18;
	if(result2pix < 18) result2pix = 18;
	
	return high;
}

function showPrefs()
{
	if(updating) return;
	var front = document.getElementById("front");
	var back = document.getElementById("back");
	
	if (window.widget)
		widget.prepareForTransition("ToBack");
	
	front.style.display="none";		// hide the front
	back.style.display="block";		// show the back
	
	if (window.widget)
		setTimeout ('widget.performTransition();', 0);		// and flip the widget over	

	document.getElementById('fliprollie').style.display = 'none';
}

function hidePrefs()
{
	var front = document.getElementById("front");
	var back = document.getElementById("back");
	
	if (window.widget)
		widget.prepareForTransition("ToFront");
	
	back.style.display="none";			// hide the back
	front.style.display="block";		// show the front
	
	if (window.widget)
		setTimeout ('widget.performTransition();', 0);		// and flip the widget back to the front
}

function findChild (element, nodeName) {
	var child;
	
	for (child = element.firstChild; child != null; child = child.nextSibling) {
		if (child.nodeName == nodeName)
			return child;
	}
	
	return null;
}

function launchFTW() {
	if(window.widget) {
		var front = document.getElementById("front");
		var back = document.getElementById("back");
		back.style.display="none";			// hide the back
		front.style.display="block";		// show the front
		widget.openURL("http://softwareftw.kzoomedia.com/");
	}
}

function donate() {
	if(window.widget) {
		var front = document.getElementById("front");
		var back = document.getElementById("back");
		back.style.display="none";			// hide the back
		front.style.display="block";		// show the front
		widget.openURL("https://www.paypal.com/cgi-bin/webscr?cmd=_xclick&business=admin%40kzoomedia%2ecom&no_shipping=0&no_note=1&tax=0&currency_code=USD&bn=PP%2dDonationsBF&charset=UTF%2d8");
	}
}
 	
function loaded() {
	if(!window.widget) {
		show();
	}
}

function onHide () {

}

function showDownload() {
	var contents = document.getElementById('content');
	while (contents.hasChildNodes()) {
		contents.removeChild(contents.firstChild);
	}
	error = document.getElementById('error');
	error.style.display = "none";
	error = null;
	spinner = document.getElementById("spinner");
	spinner.style.display = "block";
}

function showError(error, desc) {
	var search1 = document.getElementById('search1');
	var search2 = document.getElementById('search2');
	//search1.value = search2.value = "";
	if(search1_xml != null)
		search1_xml.abort();
	if(search2_xml != null)
		search2_xml.abort();
	search1_xml = search2_xml = null;
	result1 = result2 = 0;
	updating = false;
	
	resetVars();
	
	var contents = document.getElementById('content');
	while (contents.hasChildNodes()) {
		contents.removeChild(contents.firstChild);
	}
	var reason = document.createElement('div');
	reason.setAttribute('id', 'reason');
	reason.innerHTML = desc;
	contents.appendChild(reason);
	error = document.getElementById('error');
	error.style.display = "block";
}

function mousemove (event)
{
	if (!flipShown)			// if the preferences flipper is not already showing...
	{
		if (animation.timer != null)			// reset the animation timer value, in case a value was left behind
		{
			clearInterval (animation.timer);
			animation.timer  = null;
		}
		
		var starttime = (new Date).getTime() - 13; 		// set it back one frame
		
		animation.duration = 500;												// animation time, in ms
		animation.starttime = starttime;										// specify the start time
		animation.firstElement = document.getElementById ('status');			// specify the element to fade
		animation.timer = setInterval ("animate();", 13);						// set the animation function
		animation.from = animation.now;											// beginning opacity (not ness. 0)
		animation.to = 1.0;														// final opacity
		animate();																// begin animation
		flipShown = true;														// mark the flipper as animated
	}
}

function mouseexit (event)
{
	if (flipShown)
	{
		// fade in the flip widget
		if (animation.timer != null)
		{
			clearInterval (animation.timer);
			animation.timer  = null;
		}
		
		var starttime = (new Date).getTime() - 13;
		
		animation.duration = 500;
		animation.starttime = starttime;
		animation.firstElement = document.getElementById ('status');
		animation.timer = setInterval ("animate();", 13);
		animation.from = animation.now;
		animation.to = 0.0;
		animate();
		flipShown = false;
	}
}

function animate()
{
	var T;
	var ease;
	var time = (new Date).getTime();
		
	
	T = limit_3(time-animation.starttime, 0, animation.duration);
	
	if (T >= animation.duration)
	{
		clearInterval (animation.timer);
		animation.timer = null;
		animation.now = animation.to;
	}
	else
	{
		ease = 0.5 - (0.5 * Math.cos(Math.PI * T / animation.duration));
		animation.now = computeNextFloat (animation.from, animation.to, ease);
	}
	
	animation.firstElement.style.opacity = animation.now;
}

function limit_3 (a, b, c)
{
    return a < b ? b : (a > c ? c : a);
}

function computeNextFloat (from, to, ease)
{
    return from + (to - from) * ease;
}

// these functions are called when the info button itself receives onmouseover and onmouseout events

function enterflip(flip)
{
	document.getElementById(flip).style.display = 'block';
}

function exitflip(flip)
{
	document.getElementById(flip).style.display = 'none';
}
