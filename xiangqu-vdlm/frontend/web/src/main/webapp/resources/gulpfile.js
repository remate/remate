//mate start
var gulp = require('gulp');
var autowatch = require('gulp-autowatch');
var less = require('gulp-less');
var path = require('path');
var shell = require('gulp-shell');
var minifyCSS = require('gulp-minify-css');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var imageisux = require('gulp-imageisux');
var dirpath = "/after/";

var paths = {
    "less": "./less/**/*.*",
    'js': './js/**/*.*'
}

gulp.task('watch', function(cb) {
    autowatch(gulp, paths);
    return cb();
});

gulp.task('js', shell.task([
    'spm build '
    //'spm build --debug'
]));
gulp.task('javascript', function() {
    return gulp.src('./js/**/*.js')
        .pipe(uglify())
        .pipe(concat('all.min.js'))
        .pipe(gulp.dest('./dist/'));
});


gulp.task('less', function() {

    gulp.src('./less/**/*.less')
        .pipe(less({
            paths: [path.join(__dirname, 'less')]
        }))
        .pipe(gulp.dest('css'))
        .pipe(minifyCSS())
        .pipe(gulp.dest('css'))
});


gulp.task('imageisux', function() {
    return gulp.src(['./images/*'])
        .pipe(imageisux('/dest/',true));
});


var enableWebp = true;
gulp.task('imageisux', function() {
    return gulp.src(['img/*'])
        .pipe(imageisux(dirpath,enableWebp));
});
//mate end


