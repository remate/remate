<template>
    <div class="shop-cart">
        <div class="content">
            <div class="content-left">
                <div class="logo-wrapper">
                    <div class="logo" :class="{highlight:totalCount>0}">
                        <span class="icon-yahoo"></span>
                    </div>
                    <div class="num" v-show="totalCount>0">{{totalCount}}</div>
                </div>
                <div class="price" :class="{highlight:totalPrice>0}">￥{{totalPrice}}</div>
                <div class="desc">需配送费{{deliveryPrice}}元</div>
            </div>
            <div class="content-right">
                <div class="pay" :class="payClass">{{payDesc}}</div>
            </div>
        </div>
    </div>
</template>

<script type="text/ecmascript-6">
    /* eslint-disable */
    export default{
        props:{
            selectFoods:{
              type:Array,
                default(){
                  return [{price:10,count:22}];
                }
            },
            deliveryPrice:{
                type:Number,
                default:0//设置默认值
            },
            minPrice:{
                type:Number,
                default:0
            }
        },
        computed:{
            totalPrice(){
                var total = 0;
                this.selectFoods.forEach((food) => {
                    total += food.price * food.count;
                });
                return total;
            },
            totalCount(){
                var count = 0;
                this.selectFoods.forEach((food) => {
                    count += food.count;
                });
                return count;
            },
            payDesc(){
                if(this.totalPrice === 0){
                    return `￥${this.minPrice}元起送`;
                } else if (this.totalPrice < this.minPrice) {
                    var diff = this.minPrice - this.totalPrice;
                    return `还差￥${diff}元起送`;
                } else {
                    return '去结算';
                }
            },
            payClass(){
                if(this.totalPrice < this.minPrice){
                    return 'not-enough';
                } else {
                    return 'enough';
                }
            }
        }

    }
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
                position relative
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
                    &.highlight
                        background rgb(10,160,220)
                        .icon-yahoo:before
                            color: #fff
                .num
                    position absolute
                    color: #fff
                    top:0
                    right:0
                    font-size: 9px
                    width: 24px
                    height: 16px
                    line-height 16px
                    text-align center
                    border-radius 16px
                    font-weight 700
                    background rgb(240,20,20)
                    box-shadow 0 4px 8px 0 rgba(0,0,0,.4)
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
                &.highlight
                    color: #fff
            .desc
                display inline-block
                vertical-align top
                line-height 24px
                margin-top 13px
                padding-left 12px
                box-sizing border-box
                font-size 10px
                font-weight 700px
                color #80858a
        .content-right
            flex 0 0 102
            width:105px
            .pay
                height: 48px
                line-height 48px
                font-size 12px
                font-weight 700
                color: #80858a
                text-align center
                background #2b333b
                &.enough
                    background #00b43c
                    color: #fff
                &.not-enough
                    background #2b333b
</style>
