$(document).ready(function() {
	'use strict';

	$('#form').submit(function(e) {

		if(!$('#use_ajax').is(':checked')) {
			return;
		}

		e.preventDefault();

		var form = $(this);

		$.ajax({
			type	: 'POST',
			cache	: false,
			url		: form.attr('action'),
			data	: form.serialize(),
			success	: function(data) {
				$("#entries").append('<div>' + data + '</div>');

				// fix index
				var index = $('#entries div[id^="entry"]').length;
				var textArray = $(data).find('h3').text().split('.', 2);

				$('#entries div[id^="entry"]:last').find('h3').text(index + '.' + textArray[1]);
				$('html, body').animate({scrollTop: form.offset().top}, 2000);

				e.target.reset();
			}
		});
	});

	$('#entries').on('submit','form', function(e){

		if(!$('#use_ajax').is(':checked')) {
			return;
		}

		e.preventDefault();

		var form = $(this);
		var id = form.attr('data-entry-id');

		$.ajax({
			type	: 'DELETE',
			cache	: false,
			url		: form.attr('action'),
			data	: form.serialize(),
			success	: function() {

				$('#entry' + id).slideUp(500, function() {
					var followingEntries = $(this).parent().nextAll().each(function() {
						var textArray = $(this).find('h4').text().split('.', 2);
						$(this).find('h4').text((parseInt(textArray[0],10)-1) + '.' + textArray[1]);
					});

					$(this).parent().remove();
				});
			}
		});
	});
});
