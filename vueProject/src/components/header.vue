<template>
    <div class="header">
        <div class="content-wrapper">
            <div class="avatar">
                <img width="64" height="64" :src="seller.avatarPath"/>
            </div>
            <div class="content">
                <div class="title">
                    <span class="brand">品牌</span>
                    <span class="name">{{seller.name}}</span>
                </div>
                <div class="description">
                    {{seller.description}}/{{seller.deliveryTime | deliveryTime}}
                </div>
                <div v-if="seller.supports" class="support">
                    <span class="icon">减</span>
                    <span class="text">{{seller.supports[0].description}}</span>
                </div>
            </div>
            <div v-if="seller.supports" class="support-count">
                <span class="count">{{seller.supports.length}}个</span>
                <span class="icon-circle-right"></span>
            </div>
        </div>
        <div class="bulletin-wrapper">
            <span class="bulletin-title">公告</span><span class="bulletin-text">{{seller.bulletin}}</span>
            <span class="icon-circle-right"></span>
        </div>
        <div class="background">
            <img :src="seller.avatarPath" width="100%" height="100%">
        </div>
    </div>
</template>

<script type="text/ecmascript-6">
    // eslint-disable-next-line
    /* eslint-disable */
    export default {
       /*props: ['seller'],
        data() {
            return {
                seller: this.seller
            }
        }   方法一不采用对象模式*/
       props:{
           seller:{
               type:Object
           }
       },   //方法一采用对象模式  type为传入输入的验证类型
        filters:{
           deliveryTime:function(time){
               return time+"分钟送达";
           }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    @font-face
        font-family: 'icomoon'
        src:  url('/static/fonts/icomoon.eot?lndju3')
        src:  url('/static/fonts/icomoon.eot?lndju3#iefix') format('embedded-opentype'),
                url('/static/fonts/icomoon.ttf?lndju3') format('truetype'),
                url('/static/fonts/icomoon.woff?lndju3') format('woff'),
                url('/static/fonts/icomoon.svg?lndju3#icomoon') format('svg');
        font-weight: normal
        font-style: normal


    [class^="icon-"], [class*=" icon-"]
        /* use !important to prevent issues with browser extensions that change fonts */
        font-family: 'icomoon' !important
        speak: none
        font-style: normal
        font-weight: normal
        font-variant: normal
        text-transform: none
        line-height: 1
        /* Better Font Rendering =========== */
        -webkit-font-smoothing: antialiased
        -moz-osx-font-smoothing: grayscale

    .header
        color #fff
        position relative
        background rgba(7,17,27,.5)
        /*background #AB9795*/
        .content-wrapper
            position relative
            padding 24px 12px 18px 24px
            font-size 0
            .avatar
                vertical-align top
                display inline-block
                img
                    border-radius 4px
            .content
                vertical-align top
                font-size 12px
                margin-left 16px
                display inline-block
                .title
                    margin 2px 0 8px 0
                    .brand
                        width 30px
                        height 18px
                        line-height 18px
                        text-align center
                        display inline-block
                        background orangered
                    .name
                        margin-left 6px
                        font-size 18px
                        font-weight bold
                .description
                    margin-bottom 10px
                    line-height 12px
                    font-size 12px
                .support
                    .icon
                        vertical-align top
                        display inline-block
                        width 12px
                        height 12px
                        margin-right 4px
                        background darkorange
                    .text
                        font-size 10px
                        line-height 12px
            .support-count
                position: absolute
                bottom 14px
                right 12px
                padding 0 8px
                height: 24px
                line-height 24px
                border-radius 14px
                background rgba(0,0,0,.2)
                text-align center
                .count
                    font-size 12px
                    vertical-align top
                .icon-circle-right
                    &:before
                      line-height 24px
                      margin-left 2px
                      font-size 10px
                      content "\ea42"
        .bulletin-wrapper
            position relative
            height 28px
            line-height 28px
            padding 0 22px
            white-space nowrap
            overflow hidden
            text-overflow ellipsis
            background rgba(101,79,89,.5)
            .bulletin-title
                display inline-block
                vertical-align top
                font-size 12px
                width 25px
                height 12px
                line-height 12px
                background #fff
                color: #000
                border-radius 2px
                margin-top 7px
            .bulletin-text
                vertical-align top
                font-size 12px
                margin 0 4px
            .icon-circle-right
                &:before
                    position absolute
                    right 6px
                    bottom 2px
                    line-height 24px
                    margin-left 2px
                    font-size 10px
                    content "\ea42"
        .background
            position: absolute
            top 0
            left 0
            width 100%
            height 100%
            z-index -1
            filter blur(10px)

</style>
