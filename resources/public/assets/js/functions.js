//
// Functions and functions and functions...
//
$(document).on('htmx:beforeOnLoad', (event) => {
    const { status, response } = event.detail.xhr;
    if (status >= 400) {
        event.stopPropagation();
        $('#systemMessage').html(response);
        $('#errorModal').modal('show');
    }
});
