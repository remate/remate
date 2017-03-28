<template>
   <div class="goods">
       <div class="menu-wrapper" ref="menuWrapper">
            <ul>
                <li v-if="goods" class="menu-list" :class="{active: currentIndex==i?true:false}" v-for="(item,i) in goods" @click="goodsTab(i,$event)">
                    <span class="border-1px">{{item.name}}</span>
                </li>
            </ul>
       </div>
       <div class="foods-wrapper" ref="foodsWrapper">
            <ul>
                <li v-for="item in goods" class="food-list food-list-hook">
                    <h1 class="title">{{item.name}}</h1>
                    <ul>
                        <li v-for="food in item.foods" class="food-item border-1px">
                            <div class="icon">
                                <img width="57px" height="57px" :src="food.image"/>
                            </div>
                            <div class="content">
                                <h2 class="name">{{food.name}}</h2>
                                <p class="desc">{{food.description}}</p>
                                <div class="extra">
                                    <span class="count">月售{{food.sellCount}}份</span><span>好评率{{food.rating}}%</span>
                                </div>
                                <div class="price">
                                    <span class="now">￥{{food.price}}</span><span class="old" v-show="food.oldPrice">￥{{food.oldPrice}}</span>
                                </div>
                            </div>
                        </li>
                    </ul>
                </li>
            </ul>
       </div>
   </div>
</template>
<script type="text/ecmascript-6">
    /* eslint-disable */
    import BScroll from "better-scroll";
    export default {
//        props:['seller','goodthings'],
        data(){
            return {
                goods:{},
                listHeight:[],
                scrollY:0
            }
        },
        computed:{
            currentIndex(){
                for(var i = 0; i < this.listHeight.length; i++){
                    var height1 = this.listHeight[i];
                    var height2 = this.listHeight[i+1];
                    if( !height2 || (this.scrollY >= height1 && this.scrollY < height2) ){
                        return i;
                    }
                }
                return 0;
            }
        },
        methods:{
            goodsTab(i,event){
                if(event._constructed){
                    return;
                }
                var foodList = this.$refs.foodsWrapper.getElementsByClassName('food-list-hook');
                var el = foodList[i];
                this.foodsScroll.scrollToElement(el,300)
            },
            _initScroll(){
//                new BScroll(document.querySelector('.menu-wrapper'),{});
//                new BScroll(document.querySelector('.foods-wrapper'),{});
                this.menuScroll = new BScroll(this.$refs.menuWrapper,{
                    click:true
                });
                this.foodsScroll = new BScroll(this.$refs.foodsWrapper,{
                    probeType:3
                });
                this.foodsScroll.on('scroll',(pos) => {
                    this.scrollY = Math.abs(Math.round(pos.y));
                })
            },
            _calculateHeight(){
                var foodList = this.$refs.foodsWrapper.getElementsByClassName('food-list-hook');
                var height = 0;
                this.listHeight.push(height);
                for(var i = 0; i<foodList.length; i++){
                    var item = foodList[i];
                    height += item.clientHeight;
                    this.listHeight.push(height);
//                    console.log(this.listHeight);
                }
            }
        },
        created() {
            this.$http.get('/static/api/seller.json').then((res) => {
                var response = res.body;
                this.goods = response.goods;
                this.$nextTick(() => {
                    this._initScroll();
                    this._calculateHeight();
                })
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
                    background #fff
                    font-weight 500
                    span
                        border-1px(#fff)
        .foods-wrapper
            flex:1
            .title
                padding-left 14px
                height: 26px
                line-height 26px
                border-left 2px solid #dedde1
                font-size 12px
                color: rgb(147,153,159)
                background #f3f5f7
            .food-item
                display flex
                margin:18px
                padding-bottom 18px
                border-1px(rgba(7,17,27,.1))
                &:last-child
                    border-none()
                    margin-bottom 0
                .icon
                    flex 0 0 57px
                    margin-right 10px
                .content
                    flex:1
                    .name
                        margin:2px 0 8px 0
                        height: 14px
                        line-height 14px
                        font-size 14px
                        color rgb(7,17,27)
                    .desc, .extra
                        line-height 10px
                        font-size:10px
                        color: rgb(147,153,159)
                    .desc
                        line-height 13px
                        margin-bottom:8px
                    .extra
                        .count
                            margin-right 12px
                    .price
                        font-weight 700
                        line-height 24px
                        .now
                            margin-right 8px
                            font-size 14px
                            color rgb(240,20,20)
                        .old
                            text-decoration line-through
                            font-size 10px
                            color: rgb(147,153,159)
</style>
