Unfluffed.Component(function(app) {
    app.subscribe('/hello', function(message) {
        $('body').append($('<p>').text(message.greeting));
    }).then(function() {
        app.publish('/hello', {greeting: 'Hello, world!'});
    });
});
