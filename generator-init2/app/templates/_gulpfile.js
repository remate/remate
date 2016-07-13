var gulp=require('gulp'),
    clean = require('gulp-clean'),
    uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    rename = require('gulp-rename'),
    minify = require('gulp-minify-css'),
    sass = require("gulp-sass");
var myFile={
    "scss":{
        src:'src/scss',
        dist:'build/css'
    },
    "js":{
        src:'src/js',
        dist:'build/js'
    },
    "cleanFile":{
        dist:'build/'
    }
};
gulp.task('clean',function(){
    return gulp.src([myFile.cleanFile.dist],{read:false})
        .pipe(clean({force:true}));
});
gulp.task('sass',function(){
    gulp.src([myFile.scss.src+'/index.scss'])
        .pipe(sass())
        .pipe(minify())
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest(myFile.scss.dist))
});
gulp.task('minify',function () {
    gulp.src([myFile.js.src+'/index.js', myFile.js.src+'/jquery.js',])
        .pipe(uglify())
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest(myFile.js.dist))
});
gulp.task('watch',function(){
    gulp.watch([myFile.scss.src+'/*.scss',myFile.js.src+'/*.js'],['default_after']);
});
gulp.task('default',['clean'],function(){
    gulp.start('sass','minify','watch');
});
gulp.task('default_after',['clean'],function(){
    gulp.start('sass','minify');
});