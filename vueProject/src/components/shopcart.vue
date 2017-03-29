<template>
    <div class="shop-cart">
        <div class="content">
            <div class="content-left">
                <div class="logo-wrapper">
                    <div class="logo">
                        <span class="icon-yahoo"></span>
                    </div>
                </div>
                <div class="price">￥10</div>
                <div class="desc">配送费2元</div>
            </div>
            <div class="content-right"></div>
        </div>
    </div>
</template>

<script type="text/ecmascript-6">
    /* eslint-disable */
    export default{}
</script>

<style lang="stylus" rel="stylesheet/stylus">
.shop-cart
    position fixed
    left: 0
    bottom: 0
    z-index 50
    width:100%
    height:48px
    .content
        display flex
        background #141d27
        font-size 0
        .content-left
            flex:1
            .logo-wrapper
                display inline-block
                vertical-align top
                position: relative
                top: -10px
                margin 0 12px
                padding: 6px
                width: 56px
                height: 56px
                box-sizing border-box
                border-radius 50%
                background #141d27
                .logo
                    width:100%
                    height:100%
                    background #2b343c
                    border-radius 50%
                    text-align center
                    .icon-yahoo:before
                        content: "\eabb"
                        color: #80858a
                        font-size 24px
                        line-height 44px
            .price
                display inline-block
                vertical-align top
                line-height 24px
                margin-top 12px
                padding-right 12px
                box-sizing border-box
                border-right 1px solid rgba(255,255,255,.1)
                font-size 16px
                font-weight 700px
                color #80858a
            .desc
                display inline-block
                vertical-align top
                line-height 24px
                margin-top 13px
                padding-left 12px
                box-sizing border-box
                font-size 12px
                font-weight 700px
                color #80858a
        .content-right
            flex 0 0 105
            width:105px

</style>
