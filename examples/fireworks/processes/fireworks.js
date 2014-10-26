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
        app.publish('/firework', {
            target: {
                y: (event.clientY || event.changedTouches[0].clientY) + (Math.random() * 100)
            },
            velocity: {
                x: Math.random() * 3 - 1.5
            },
            color: Math.floor(Math.random() * 100) * 12
        });
    }

    document.addEventListener('mouseup', createFirework, true);
    document.addEventListener('touchend', createFirework, true);

    app.subscribe('/firework', function(particle) {
        fireworks.createParticle(
            particle.position, particle.target, particle.velocity, particle.color, particle.usePhysics);
    });
});
