Unfluffed.Process(function(app) {
    function imageElement(src) {
        var img = document.createElement('img');
        img.src = src;
        return img;
    }

    var fireworks = new Fireworks({
        bigGlow: imageElement(app.client.asset('Fireworks/images/big-glow.png')),
        smallGlow: imageElement(app.client.asset('Fireworks/images/small-glow.png'))
    });

    fireworks.initialize();

    function createFirework(event) {
        var x = event.clientX || event.changedTouches[0].clientX,
            y = event.clientY || event.changedTouches[0].clientY;

        app.publish('/firework', {
            x: x / window.innerWidth,
            y: y / window.innerHeight,
            color: Math.floor(Math.random() * 100) * 12
        });
    }

    document.addEventListener('mouseup', createFirework, true);
    document.addEventListener('touchend', createFirework, true);

    app.subscribe('/firework', function(firework) {
        fireworks.createParticle(
            null,
            {y: (firework.y * window.innerHeight)},
            {x: ((firework.x * window.innerWidth) - window.innerWidth / 2) / 100},
            firework.color);
    });
});
