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
            <div v-if="seller.supports" class="support-count" @click="showDetail">
                <span class="count">{{seller.supports.length}}个</span>
                <span class="icon-circle-right"></span>
            </div>
        </div>
        <div class="bulletin-wrapper" @click="showDetail">
            <span class="bulletin-title">公告</span><span class="bulletin-text">{{seller.bulletin}}</span>
            <span class="icon-circle-right"></span>
        </div>
        <div class="background">
            <img :src="seller.avatarPath" width="100%" height="100%">
        </div>
        <div class="detail" v-show="detailShow">
            <div class="detail-wrapper">
                <div class="detail-main">
                    <div class="name">{{seller.name}}</div>
                    <v-star :score="seller.score"></v-star>
                </div>
                <div class="title">
                    <div class="line"></div>
                    <div class="text"></div>
                    <div class="line"></div>
                </div>
            </div>
            <div class="detail-close" @click="hideDetail">
                <i class="close">X</i>
            </div>
        </div>
    </div>
</template>

<script type="text/ecmascript-6">
    // eslint-disable-next-line
    /* eslint-disable */
    import star from './star.vue';
    export default {
       /*props: ['seller'],*/
        data() {
            return {
                detailShow:false
            }
        },   //方法一不采用对象模式
        methods:{
            showDetail() {
                this.detailShow = true;
            },
            hideDetail() {
                this.detailShow = false;
            }
        },
       props:{
           seller:{
               type:Object
           }
       },   //方法一采用对象模式  type为传入输入的验证类型
        filters:{
           deliveryTime:function(time){
               return time + "分钟送达";
           }
        },
        components:{
            'v-star':star
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    @font-face
        font-family: 'icomoon'
        src:  url('/static/fonts/icomoon.eot?myorls');
        src:  url('/static/fonts/icomoon.eot?myorls#iefix') format('embedded-opentype'),
            url('/static/fonts/icomoon.ttf?myorls') format('truetype'),
            url('/static/fonts/icomoon.woff?myorls') format('woff'),
            url('/static/fonts/icomoon.svg?myorls#icomoon') format('svg')
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
        overflow hidden
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
                margin-top 8px
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
        .detail
            position fixed
            top 0
            left 0
            z-index 100
            width 100%
            height 100%
            overflow auto
            background rgba(1,17,27,.8)
            .detail-wrapper
                min-height 100%
                overflow hidden
                .detail-main
                    margin-top 64px
                    padding-bottom 64px
                    .name
                        line-height 16px
                        text-align center
                        font-size 16px
                        font-weight 700
                    .title
                        display flex
                        width:80%
                        margin:30px auto 24px auto
                        .line
                            flex 1
                            position relative
                            top: -6px
                            border-bottom 1px solid rgba(255,255,255,.2)
                        .text
                            padding: 0 12px
                            font-size 14px
            .detail-close
                position relative
                width 32px
                height 32px
                line-height 32px
                margin -50px auto 0 auto
                clear: both
                font-size 20px
                text-align center
                border-radius 16px
                border:1px solid #fff
</style>
