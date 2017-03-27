<template>
   <div class="goods">
       <div class="menu-wrapper">
            <ul>
                <li v-if="goods" class="menu-list" :class="{active: isActive==i?true:false}" v-for="(item,i) in goods" @click="goodsTab(i)">
                    <span class="border-1px">{{item.name}}</span>
                </li>
            </ul>
       </div>
       <div class="foods-wrapper">

       </div>
   </div>
</template>
<script type="text/ecmascript-6">
    /* eslint-disable */
    export default {
//        props:['seller','goodthings'],
        data(){
            return {
                goods:{},
                isActive:0
            }
        },
        methods:{
            goodsTab(i){
                this.isActive = i;
            }
        },
        created() {
            this.$http.get('/static/api/seller.json').then((res) => {
                var response = res.body;
                this.goods = response.goods;
            })
        }
    }
</script>

<style lang="stylus" rel="stylesheet/stylus">
    @import "../../static/border_1px.styl"

    .goods
        position: absolute
        display flex
        top: 174px
        bottom: 46px
        width:100%
        overflow hidden
        .menu-wrapper
            flex 0 0 80px
            width:80px
            background #f3f5f7
            .menu-list
                display table
                height: 54px
                width: 56px
                line-height 14px
                padding 0 12px
                span
                    display table-cell
                    width: 56px
                    text-align center
                    border-1px(#fff)
                    vertical-align middle
                    font-size 12px
                &.active
                    background #c7c5c5
                    span
                        border-1px(#c7c5c5)
        .foods-wrapper
            flex:1
</style>
