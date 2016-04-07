/**
 * Created by ziyu on 2016/1/21.
 */
var gulp = require('gulp'),
    clean = require('gulp-clean'),
    sass = require('gulp-sass'),
    group = require('gulp-group-files'),
    minifycss = require('gulp-minify-css'),//css压缩
    uglify = require('gulp-uglify'),//js压缩
    concat = require('gulp-concat'),//文件合并
    livereload = require('gulp-livereload'),
    rename = require('gulp-rename');//文件更名;

var srcSass = {
    "base":{
      src:'scss/base.scss',
        dest:'css/scss'
    },
    "myAddress":{
        src:'scss/my_address.scss',
        dest:'css/scss/'
    },
    "orderSuccess":{
        src:'scss/order_success.scss',
        dest:'css/scss/'
    },
    "cart":{
        src:'scss/cart/cart.scss',
        dest:'css/scss/cart/'
    },
    "orderDetail":{
        src:'scss/order_detail/order_detail.scss',
        dest:'css/scss/order_detail/'
    }
};

gulp.task('clean',function(){
    gulp.src([srcSass.myAddress.dest],{read:false})
        .pipe(clean());
});
gulp.task('sass',function(){
    return group(srcSass,function(key,fileset){
        return gulp.src(fileset.src)
            .pipe(sass({outputStyle:'expanded'}))
            .pipe(gulp.dest(fileset.dest))
            //.pipe(minifycss())
            .pipe(gulp.dest(fileset.dest))
            .pipe(livereload());
    })();
});

gulp.task('js',function(){
    return gulp.src('js/dev/xiangqu/**/*.js')
        .pipe(gulp.dest('js/xiangqu'))
        //.pipe(uglify())
        .pipe(gulp.dest('js/xiangqu'))
})

gulp.task('watch',function(){
    livereload.listen();
   gulp.watch('scss/**/*.scss',['sass']);
   gulp.watch('js/dev/xiangqu/**/*.js',['js']);
});

gulp.task('default',['clean','sass']);
