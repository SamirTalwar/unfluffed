$(function() {
    var client = new Faye.Client('/bayeux');

    client.subscribe('/hello', function(message) {
        $('#body').append($('<p>')
            .append($('<span>').text('Server Says: '))
            .append($('<span>').text(message.greeting)));
    });

    client.on('transport:up', function() {
        $('#body').append($('<p>').text('Connection Established'));
        setTimeout(function() {
            client.publish('/service/hello', { name: 'World' });
        }, 100);
    });

    client.on('transport:down', function() {
        $('#body').append($('<p>').text('Connection Lost'));
    });
});
