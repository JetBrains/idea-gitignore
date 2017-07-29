$('.menu .item[data-title]').popup({
  position: 'bottom center',
});

const ides = [
  'IntelliJ IDEA', 'PhpStorm', 'WebStorm', 'PyCharm', 'RubyMine', 'AppCode',
  'CLion', 'Gogland', 'DataGrip', 'Rider', 'Android Studio',
];

const fade = () => {
  const ide = $('.ide');
  const index = (ides.indexOf(ide.text()) + 1) % ides.length;
  ide.delay(1000).fadeOut('slow', () => ide.text(ides[index])).fadeIn('slow', fade);
};

fade();
