
$(document).ready(function() {

	var message = "times=1";
	$("#button1").click(function() {
		// console.log(message);
		$.post("http://localhost:8080", message, function(response) {
			console.log(response);
		}, "text");
	});

});