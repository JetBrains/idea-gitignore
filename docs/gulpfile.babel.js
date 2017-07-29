import gulp from 'gulp';
import babel from 'gulp-babel';
import concat from 'gulp-concat';
import gulpIf from 'gulp-if';
import sass from 'gulp-sass';
import uglify from 'gulp-uglify';

const paths = {
  sassInclude:  ['./node_modules'],
  sass:         ['./scss/*.scss'],
  js:           ['./js/*.js'],
  jsLib:        [
    './node_modules/jquery/dist/jquery.min.js',
    './node_modules/semantic-ui-css/semantic.min.js',
  ],
  copy:         ['./node_modules/semantic-ui-css/themes/default/assets/**/*'],
  dist:         './assets',
};

gulp.task('watch', () => {
  gulp.watch(paths.sass, ['styles']);
  gulp.watch(paths.js, ['js']);
});

const jsTask = (path, name, min) => gulp
  .src(path)
  .pipe(concat(name))
  .pipe(gulpIf(min, babel()))
  .pipe(gulpIf(min, uglify()))
  .pipe(gulp.dest(paths.dist));

gulp.task('js', () => jsTask(paths.js, 'scripts.js', true));
gulp.task('js:lib', () => jsTask(paths.jsLib, 'libs.js'));

gulp.task('styles', () => gulp.src(paths.sass)
  .pipe(sass({
    outputStyle: 'compressed',
    includePaths: paths.sassInclude,
    importer: (url, prev, done) => done({ file: url.substr(+url.startsWith('~')) }),
  }).on('error', sass.logError))
  .pipe(gulp.dest(paths.dist)));

gulp.task('copy', () =>
  gulp.src(paths.copy)
    .pipe(gulp.dest(paths.dist))
);
gulp.task('default', () => {
  gulp.start('watch', 'js', 'js:lib', 'styles', 'copy');
});
