import gulp from 'gulp';
import sass from 'gulp-sass';
import uglify from 'gulp-uglify';

const paths = {
  sassInclude: ['./node_modules'],
  sass: './scss/*.scss',
  js: './js/*.js',
  copy: [
    './node_modules/semantic-ui-css/themes/default/assets/fonts/*',
  ],
  dist: './assets',
};

gulp.task('watch', () => {
  gulp.watch(paths.sass, ['sass']);
  gulp.watch(paths.js, ['js']);
});

gulp.task('js', () =>
  gulp.src(paths.js)
    .pipe(uglify())
    .pipe(gulp.dest(paths.dist))
);

gulp.task('copy', () =>
  gulp.src(paths.copy)
    .pipe(gulp.dest(paths.dist))
);

gulp.task('sass', () =>
  gulp.src(paths.sass)
    .pipe(sass({
      outputStyle: 'compressed',
      includePaths: paths.sassInclude,
      importer: (url, prev, done) => done({ file: url.substr(+url.startsWith('~')) }),
    }).on('error', sass.logError))
    .pipe(gulp.dest(paths.dist))
);

gulp.task('default', () => {
  gulp.start('watch', 'js', 'sass', 'copy');
});
