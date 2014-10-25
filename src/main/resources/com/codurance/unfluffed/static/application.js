$(function() {
    var client = new Faye.Client('/bayeux'),
        id = 0;

    client.addExtension({
      incoming: function(message, callback) {
        console.log('incoming', message);
        callback(message);
      },
      outgoing: function(message, callback) {
        console.log('outgoing', message);
        callback(message);
      }
    });

    function hello() {
        client.publish('/say/hello', {id: id});
    }

    client.on('transport:up', function() {
        $('#body').append($('<p>').text('Connection Established'));
        if (!id) {
            client.publish('/client/identification/request', {})
                .then(hello);
        } else {
            hello();
        }
    });

    client.subscribe('/client/identification/response', function(message) {
        id = message.clientId;
        $('#body').append($('<p>').text('I am client ' + id));
    });

    client.subscribe('/hello', function(message) {
        $('#body').append($('<p>').text(message.greeting));
    });

    client.on('transport:down', function() {
        $('#body').append($('<p>').text('Connection Lost'));
    });
});
