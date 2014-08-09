$(document).ready(function() {
	
	// Twist form submission into AJAX request
	
	$('#form').submit(function(e) {
		e.preventDefault();
		$.ajax({
			type     : "POST",
			cache    : false,
			url      : $('#form').attr('action'),
			data     : $('#form').serialize(),
			success  : function(data) {
				$("#entries").append(data);
				e.target.reset();
			}});
		});
})