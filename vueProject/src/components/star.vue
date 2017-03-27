<template>
    <div class="star">
        <span v-for="(itemClass,i) in itemClasses" class="icon-star-full" :class="itemClass" >{{i}}</span>
    </div>
</template>

<script type="text/ecmascript-6">
    /* eslint-disable */
    var LENGTH = 5;
    var CLS_ON = "on";
    var CLS_OFF = "off";
    var CLS_HALF = "half";
    export default {
        props: {
            score: {
                type: Number
            },
            size:{
                type:Number
            }
        },
        computed: {
            itemClasses() {
                var result = [];
                var score = Math.floor(this.score/10);//向下取整计算完整的星星个数
                var hasDecimal = this.score % 10 !== 0;//求一半的星星个数
                for( var i = 0; i<score; i++ ){
                    result.push(CLS_ON);
                };
                if (hasDecimal) {
                    result.push(CLS_HALF);
                };
                while (result.length < LENGTH){
                    result.push(CLS_OFF);
                };
                return result;
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
.star
    font-size 0
    text-align center
    .icon-star-full
        display inline-block
        background-repeat no-repeat
        &.on:before
            color: gold
        &.half:before
            color #9E8970
        &.off:before
            color #888
        &:before {
            content: "\e9d9";
            line-height 40px
            margin-left 2px
            font-size 22px
            padding 0 5px
            color: red
        }
</style>
